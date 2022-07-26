package swm.wbj.asyncrum.global.oauth.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import swm.wbj.asyncrum.domain.userteam.member.entity.MemberRefreshToken;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRefreshTokenRepository;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.global.config.properties.AppProperties;
import swm.wbj.asyncrum.global.oauth.entity.ProviderType;
import swm.wbj.asyncrum.domain.userteam.member.entity.RoleType;
import swm.wbj.asyncrum.global.oauth.info.OAuth2UserInfo;
import swm.wbj.asyncrum.global.oauth.info.OAuth2UserInfoFactory;
import swm.wbj.asyncrum.global.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import swm.wbj.asyncrum.global.oauth.token.AuthToken;
import swm.wbj.asyncrum.global.oauth.token.TokenProvider;
import swm.wbj.asyncrum.global.oauth.utils.CookieUtil;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

/**
 * OAuth2 인증 성공 핸들러
 */
@RequiredArgsConstructor
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final AppProperties appProperties;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final MemberRefreshTokenRepository memberRefreshTokenRepository;
    private final MemberRepository memberRepository;

    /**
     *  OAuth2 인증 과정 완료 후 JWT Access Token과 Resfresh Token 생성
     *  Resfresh Token은 수정 불가능한 쿠키에 저장, Access Token은 프론트엔드 리다이렉트 URI 에 쿼리스트링에 토큰을 담아 리다이렉트
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 리다이렉트 URL 생성
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
            return;
        }

        // OAuth 인증 과정에서 생성된 인증 관련 attributes 삭제
        clearAuthenticationAttributes(request, response);

        // 리다이렉트 response 전송
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        // 초기 OAuth 로그인에 포함한 redirect_uri 주소 가져오기
        Optional<String> redirectUri = CookieUtil.getCookie(request, OAuth2AuthorizationRequestBasedOnCookieRepository.REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException("Sorry! We've got an Unauthorized Redirect URI and can't proceed with the authentication");
        }

        // 리다이렉트할 타켓 URl 결정
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        // Authentication로부터 User 정보 가져오기
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        ProviderType providerType = ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());

        OidcUser user = ((OidcUser) authentication.getPrincipal());
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        Collection<? extends GrantedAuthority> authorities = ((OidcUser) authentication.getPrincipal()).getAuthorities();

        RoleType roleType = hasAuthority(authorities, RoleType.ADMIN.getCode()) ? RoleType.ADMIN : RoleType.USER;

        // User의 id(OauthId) 정보를 기반으로 Member의 id(memberId) 가져오기
        String memberId = memberRepository.findByOauthId(userInfo.getId()).getId().toString();

        // User 정보를 기반으로 Access Token 생성
        Date now = new Date();

        AuthToken accessToken = tokenProvider.createAuthToken(
                memberId,
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        // (Member Refresh Token에 들어갈) 내부 Refresh Token 생성
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // Member Refresh Token 생성 후 DB 저장
        MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberId(memberId);

        if(memberRefreshToken != null) {
            memberRefreshToken.setRefreshToken(refreshToken.getToken());
        } else {
            memberRefreshToken = new MemberRefreshToken(memberId, refreshToken.getToken());
            memberRefreshTokenRepository.saveAndFlush(memberRefreshToken);
        }

        int cookieMaxAge = (int) refreshTokenExpiry / 60;

        // 쿠키 갱신
        CookieUtil.deleteCookie(request, response, OAuth2AuthorizationRequestBasedOnCookieRepository.REFRESH_TOKEN);
        CookieUtil.addCookie(response, OAuth2AuthorizationRequestBasedOnCookieRepository.REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        // 최종 URL 생성 (쿼리스트링에 Access Token을 담음)
        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", accessToken.getToken())
                .build().toUriString();
    }

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private boolean hasAuthority(Collection<? extends GrantedAuthority> authorities, String authority) {
        if (authorities == null) {
            return false;
        }

        for (GrantedAuthority grantedAuthority : authorities) {
            if (authority.equals(grantedAuthority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    // Only validate host and port. Let the clients use different paths if they want to
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    if(authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort()) {
                        return true;
                    }
                    return false;
                });
    }

}
