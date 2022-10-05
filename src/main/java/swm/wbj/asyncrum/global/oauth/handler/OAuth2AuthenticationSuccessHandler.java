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
import swm.wbj.asyncrum.domain.userteam.member.exeception.MemberNotExistsException;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRefreshTokenRepository;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.global.properties.AppProperties;
import swm.wbj.asyncrum.global.oauth.entity.ProviderType;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.oauth.info.OAuth2UserInfo;
import swm.wbj.asyncrum.global.oauth.info.OAuth2UserInfoFactory;
import swm.wbj.asyncrum.global.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import swm.wbj.asyncrum.global.oauth.token.AuthToken;
import swm.wbj.asyncrum.global.oauth.token.TokenProvider;
import swm.wbj.asyncrum.global.oauth.utils.CookieUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import static swm.wbj.asyncrum.global.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository.*;

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
     *  OAuth2 인증 과정 완료 후 JWT Access Token 과 Refresh Token 생성
     *  Refresh Token 은 수정 불가능한 쿠키에 저장, Access Token 은 프론트엔드 리다이렉트 URI 에 쿼리스트링에 토큰을 담아 리다이렉트
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("응답이 이미 보내졌습니다. 해당 URL [" + targetUrl + "] 로 리다이렉션이 불가능합니다.");
            return;
        }

        clearAuthenticationAttributes(request, response);

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) {
        String targetUrl = validateRedirectUrl(request);

        String memberId = getMemberIdFromAuthentication(authentication);
        RoleType roleType = getRoleTypeFromAuthorities(authentication);

        Date now = new Date();
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        AuthToken accessToken = createAccessToken(roleType, memberId, now);
        AuthToken refreshToken = createRefreshToken(now, refreshTokenExpiry);
        saveRefreshTokenInDB(memberId, refreshToken);

        renewCookie(request, response, (int) refreshTokenExpiry, refreshToken);

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", accessToken.getToken())
                .build().toUriString();
    }

    private RoleType getRoleTypeFromAuthorities(Authentication authentication) {
        Collection<? extends GrantedAuthority> authorities = ((OidcUser) authentication.getPrincipal()).getAuthorities();
        return hasAuthority(authorities, RoleType.ADMIN.getCode()) ? RoleType.ADMIN : RoleType.USER;
    }

    private String getMemberIdFromAuthentication(Authentication authentication) {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        OidcUser user = ((OidcUser) authentication.getPrincipal());

        ProviderType providerType = ProviderType.valueOf(authToken.getAuthorizedClientRegistrationId().toUpperCase());
        return getMemberIdFromOAuthId(providerType, user);
    }

    private String validateRedirectUrl(HttpServletRequest request) {
        Optional<String> redirectUri = CookieUtil.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
                .map(Cookie::getValue);

        if(redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new IllegalArgumentException("승인된 리다이렉트 URI 가 아닙니다.");
        }

        return redirectUri.orElse(getDefaultTargetUrl());
    }

    private String getMemberIdFromOAuthId(ProviderType providerType, OidcUser user) {
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(providerType, user.getAttributes());
        return memberRepository.findByOauthId(userInfo.getId())
                .orElseThrow(MemberNotExistsException::new)
                .getId().toString();
    }

    private AuthToken createAccessToken(RoleType roleType, String memberId, Date now) {
        return tokenProvider.createAuthToken(
                memberId,
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );
    }

    private AuthToken createRefreshToken(Date now, long refreshTokenExpiry) {
        return tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );
    }

    private void saveRefreshTokenInDB(String memberId, AuthToken refreshToken) {
        MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberId(memberId);

        if(memberRefreshToken != null) {
            memberRefreshToken.setRefreshToken(refreshToken.getToken());
        } else {
            memberRefreshToken = new MemberRefreshToken(memberId, refreshToken.getToken());
            memberRefreshTokenRepository.saveAndFlush(memberRefreshToken);
        }
    }

    private void renewCookie(HttpServletRequest request, HttpServletResponse response,
                             int refreshTokenExpiry, AuthToken refreshToken) {
        int cookieMaxAge = refreshTokenExpiry / 60;

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);
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
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}
