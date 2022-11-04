package swm.wbj.asyncrum.domain.auth;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import swm.wbj.asyncrum.domain.auth.dto.LoginRequestDto;
import swm.wbj.asyncrum.domain.auth.dto.TokenResponseDto;
import swm.wbj.asyncrum.domain.member.entity.MemberRefreshToken;
import swm.wbj.asyncrum.domain.member.exeception.MemberNotExistsException;
import swm.wbj.asyncrum.domain.member.repository.MemberRefreshTokenRepository;
import swm.wbj.asyncrum.domain.member.repository.MemberRepository;
import swm.wbj.asyncrum.global.properties.AppProperties;
import swm.wbj.asyncrum.global.type.RoleType;
import swm.wbj.asyncrum.global.oauth.entity.UserPrincipal;
import swm.wbj.asyncrum.global.oauth.token.AuthToken;
import swm.wbj.asyncrum.global.oauth.token.TokenProvider;
import swm.wbj.asyncrum.global.oauth.utils.CookieUtil;
import swm.wbj.asyncrum.global.oauth.utils.HeaderUtil;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@RequiredArgsConstructor
@Transactional
@Service
public class AuthServiceImpl implements AuthService {

    private final AppProperties appProperties;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final MemberRefreshTokenRepository memberRefreshTokenRepository;
    private final MemberRepository memberRepository;

    public final static long THREE_DAYS_IN_MILLISECONDS = 259200000;
    public final static String REFRESH_TOKEN = "refresh_token";

    @Override
    public TokenResponseDto localLogin(HttpServletRequest request,
                                       HttpServletResponse response, LoginRequestDto requestDto) {
        Authentication authentication = getAuthentication(requestDto);
        setAuthentication(authentication);

        String memberId = getMemberId(requestDto);

        AuthToken accessToken = renewAccessToken(authentication, memberId);
        AuthToken refreshToken = createRefreshToken();

        MemberRefreshToken memberRefreshToken = getMemberRefreshToken(memberId);
        setMemberRefreshToken(memberId, refreshToken, memberRefreshToken);

        setRefreshTokenInCookie(request, response, refreshToken);

        return putAccessTokenInTokenDto(accessToken);
    }

    @Override
    public TokenResponseDto refreshToken(HttpServletRequest request, HttpServletResponse response) {
        AuthToken authToken = getAccessTokenFromRequestHeader(request);
        //validateAccessToken(authToken);

        Claims claims = authToken.getExpiredTokenClaims();
        validateAccessTokenExpired(claims);

        AuthToken authRefreshToken = getRefreshTokenFromCookie(request);
        validateRefreshToken(authRefreshToken);

        String memberId = claims.getSubject();
        RoleType roleType = RoleType.of(claims.get("role", String.class));

        MemberRefreshToken memberRefreshToken = getMemberRefreshTokenWithValidation(authRefreshToken, memberId);

        AuthToken newAccessToken = renewAccessToken(memberId, roleType);
        if (checkRefreshTokenTimeLessThenThreeDays(authRefreshToken)) {
            renewRefreshToken(request, response, memberRefreshToken);
        }

        return putAccessTokenInTokenDto(newAccessToken);
    }

    private void renewRefreshToken(HttpServletRequest request, HttpServletResponse response, MemberRefreshToken memberRefreshToken) {
        Date now = new Date();
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        AuthToken authRefreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime()+ refreshTokenExpiry)
        );

        memberRefreshToken.setRefreshToken(authRefreshToken.getToken());
        setRefreshTokenInCookie(request, response, authRefreshToken);
    }

    private boolean checkRefreshTokenTimeLessThenThreeDays(AuthToken authRefreshToken) {
        Date now = new Date();
        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

        return validTime <= THREE_DAYS_IN_MILLISECONDS;
    }

    private AuthToken renewAccessToken(String memberId, RoleType roleType) {
        Date now = new Date();

        return tokenProvider.createAuthToken(
                memberId,
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );
    }

    private MemberRefreshToken getMemberRefreshTokenWithValidation(AuthToken authRefreshToken, String memberId) {
        MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberIdAndRefreshToken(memberId, authRefreshToken.getToken());

        if (memberRefreshToken == null) {
            throw new IllegalArgumentException("Invalid Refresh Token.");
        }
        return memberRefreshToken;
    }

    private void validateRefreshToken(AuthToken authRefreshToken) {
        if (!authRefreshToken.validateToken()) {
            throw new IllegalArgumentException("Invalid Refresh Token.");
        }
    }

    private AuthToken getRefreshTokenFromCookie(HttpServletRequest request) {
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));
        return tokenProvider.convertAuthToken(refreshToken);
    }

    private void validateAccessTokenExpired(Claims claims) {
        if (claims == null) {
            throw new IllegalArgumentException("Not Expired Access Token.");
        }
    }

    private void validateAccessToken(AuthToken authToken) {
        if(!authToken.validateToken()) {
            throw new IllegalArgumentException("Invalid Access Token.");
        }
    }

    private AuthToken getAccessTokenFromRequestHeader(HttpServletRequest request) {
        String accessToken = HeaderUtil.getAccessToken(request);

        return tokenProvider.convertAuthToken(accessToken);
    }

    private TokenResponseDto putAccessTokenInTokenDto(AuthToken accessToken) {
        return new TokenResponseDto(accessToken.getToken());
    }

    private void setRefreshTokenInCookie(HttpServletRequest request, HttpServletResponse response, AuthToken refreshToken) {
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        int cookieMaxAge = (int)refreshTokenExpiry / 60;

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);
    }

    private void setMemberRefreshToken(String memberId, AuthToken refreshToken, MemberRefreshToken memberRefreshToken) {
        if (memberRefreshToken == null) {
            memberRefreshToken = new MemberRefreshToken(memberId, refreshToken.getToken());
            memberRefreshTokenRepository.saveAndFlush(memberRefreshToken);
        } else {
            memberRefreshToken.setRefreshToken(refreshToken.getToken());
        }
    }

    private MemberRefreshToken getMemberRefreshToken(String memberId) {
        return memberRefreshTokenRepository.findByMemberId(memberId);
    }

    private AuthToken createRefreshToken() {
        Date now = new Date();
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

        return tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );
    }

    private AuthToken renewAccessToken(Authentication authentication, String memberId) {
        Date now = new Date();

        return tokenProvider.createAuthToken(
                memberId,
                ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );
    }

    private String getMemberId(LoginRequestDto requestDto) {
        String email = requestDto.getEmail();

        return memberRepository.findByEmail(email)
                .orElseThrow(MemberNotExistsException::new)
                .getId().toString();
    }

    private void setAuthentication(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private Authentication getAuthentication(LoginRequestDto requestDto) {
        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(),
                        requestDto.getPassword()
                )
        );
    }
}
