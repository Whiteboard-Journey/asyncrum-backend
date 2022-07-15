package swm.wbj.asyncrum.domain.auth.controller;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import swm.wbj.asyncrum.domain.auth.dto.LoginRequestDto;
import swm.wbj.asyncrum.domain.auth.dto.TokenResponseDto;
import swm.wbj.asyncrum.domain.userteam.member.entity.MemberRefreshToken;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRefreshTokenRepository;
import swm.wbj.asyncrum.global.config.properties.AppProperties;
import swm.wbj.asyncrum.global.error.ErrorResponseDto;
import swm.wbj.asyncrum.global.oauth.entity.RoleType;
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
@RequestMapping("/api/v1/auth")
@RestController
public class AuthController {

    private final AppProperties appProperties;
    private final TokenProvider tokenProvider;
    private final AuthenticationManager authenticationManager;
    private final MemberRefreshTokenRepository memberRefreshTokenRepository;

    private final static long THREE_DAYS_IN_MILLISECONDS = 259200000;
    private final static String REFRESH_TOKEN = "refresh_token";

    @PostMapping("/login")
    public ResponseEntity<?> login(HttpServletRequest request,
                                   HttpServletResponse response,
                                   @RequestBody LoginRequestDto requestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getId(),
                        requestDto.getPassword()
                )
        );

        String userId = requestDto.getId();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Access Token 생성
        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                userId,
                ((UserPrincipal) authentication.getPrincipal()).getRoleType().getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        // Refresh Token 생성
        long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();
        AuthToken refreshToken = tokenProvider.createAuthToken(
                appProperties.getAuth().getTokenSecret(),
                new Date(now.getTime() + refreshTokenExpiry)
        );

        // userId Refresh Token 으로 DB 확인
        MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByUserId(userId);
        if (memberRefreshToken == null) {
            // 없는 경우 새로 등록
            memberRefreshToken = new MemberRefreshToken(userId, refreshToken.getToken());
            memberRefreshTokenRepository.saveAndFlush(memberRefreshToken);
        } else {
            // 기존의 정보가 있는 경우 토큰 업데이트
            memberRefreshToken.setRefreshToken(refreshToken.getToken());
        }

        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        return ResponseEntity.ok(new TokenResponseDto(accessToken.getToken()));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // Access Token 검증
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);

        if(!authToken.validateToken()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto("Invalid Access Token."));
        }

        // Expired Access Token 검증
        Claims claims = authToken.getExpiredTokenClaims();
        if (claims == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto("Not Expired Access Token."));
        }

        String userId = claims.getSubject();
        RoleType roleType = RoleType.of(claims.get("role", String.class));

        // Refresh Token 검증
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));
        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);

        if (authRefreshToken.validateToken()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto("Invalid Refresh Token."));
        }

        // userId Refresh Token 으로 DB 확인
        MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByUserIdAndRefreshToken(userId, refreshToken);

        if (memberRefreshToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponseDto("Invalid Refresh Token."));
        }

        // Access Token 또한 갱신
        Date now = new Date();
        AuthToken newAccessToken = tokenProvider.createAuthToken(
                userId,
                roleType.getCode(),
                new Date(now.getTime() + appProperties.getAuth().getTokenExpiry())
        );

        long validTime = authRefreshToken.getTokenClaims().getExpiration().getTime() - now.getTime();

        // Refresh Token 기간이 3일 이하로 남은 경우, refresh 토큰 갱신
        if (validTime <= THREE_DAYS_IN_MILLISECONDS) {
            // Refresh Token 설정
            long refreshTokenExpiry = appProperties.getAuth().getRefreshTokenExpiry();

            authRefreshToken = tokenProvider.createAuthToken(
                    appProperties.getAuth().getTokenSecret(),
                    new Date(now.getTime()+ refreshTokenExpiry)
            );

            // DB에 Refresh Token 업데이트
            memberRefreshToken.setRefreshToken(authRefreshToken.getToken());

            int cookieMaxAge = (int) refreshTokenExpiry / 60;
            CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
            CookieUtil.addCookie(response, REFRESH_TOKEN, authRefreshToken.getToken(), cookieMaxAge);
        }

        return ResponseEntity.ok(new TokenResponseDto(newAccessToken.getToken()));
    }
}
