package swm.wbj.asyncrum.domain.auth.service;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import swm.wbj.asyncrum.domain.auth.dto.LoginRequestDto;
import swm.wbj.asyncrum.domain.auth.dto.TokenResponseDto;
import swm.wbj.asyncrum.domain.userteam.member.entity.Member;
import swm.wbj.asyncrum.domain.userteam.member.entity.MemberRefreshToken;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRefreshTokenRepository;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.domain.userteam.member.service.MemberService;
import swm.wbj.asyncrum.global.config.properties.AppProperties;
import swm.wbj.asyncrum.domain.userteam.member.entity.RoleType;
import swm.wbj.asyncrum.global.mail.MailService;
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
    private final MemberService memberService;
    private final MailService mailService;

    private final static long THREE_DAYS_IN_MILLISECONDS = 259200000;
    private final static String REFRESH_TOKEN = "refresh_token";

    @Value("${server.url}")
    private String serverUrl;

    @Override
    public TokenResponseDto loginService(HttpServletRequest request,
                                         HttpServletResponse response, LoginRequestDto requestDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(),
                        requestDto.getPassword()
                )
        );

        String email = requestDto.getEmail();
        String memberId = memberRepository.findByEmail(email).getId().toString();
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Access Token 생성
        Date now = new Date();
        AuthToken accessToken = tokenProvider.createAuthToken(
                memberId,
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
        MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberId(memberId);
        if (memberRefreshToken == null) {
            // 없는 경우 새로 등록
            memberRefreshToken = new MemberRefreshToken(memberId, refreshToken.getToken());
            memberRefreshTokenRepository.saveAndFlush(memberRefreshToken);
        } else {
            // 기존의 정보가 있는 경우 토큰 업데이트
            memberRefreshToken.setRefreshToken(refreshToken.getToken());
        }

        int cookieMaxAge = (int) refreshTokenExpiry / 60;
        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);

        // Access Token JSON 형태로 response
        return new TokenResponseDto(accessToken.getToken());
    }

    @Override
    public TokenResponseDto refreshService(HttpServletRequest request, HttpServletResponse response) {

        // Access Token 검증
        String accessToken = HeaderUtil.getAccessToken(request);
        AuthToken authToken = tokenProvider.convertAuthToken(accessToken);

        if(!authToken.validateToken()) {
            throw new IllegalArgumentException("Invalid Access Token.");
        }

        // Expired Access Token 검증
        Claims claims = authToken.getExpiredTokenClaims();
        if (claims == null) {
            throw new IllegalArgumentException("Not Expired Access Token.");
        }

        String memberId = claims.getSubject();
        RoleType roleType = RoleType.of(claims.get("role", String.class));

        // Refresh Token 검증
        String refreshToken = CookieUtil.getCookie(request, REFRESH_TOKEN)
                .map(Cookie::getValue)
                .orElse((null));
        AuthToken authRefreshToken = tokenProvider.convertAuthToken(refreshToken);

        if (authRefreshToken.validateToken()) {
            throw new IllegalArgumentException("Invalid Refresh Token.");
        }

        // userId Refresh Token 으로 DB 확인
        MemberRefreshToken memberRefreshToken = memberRefreshTokenRepository.findByMemberIdAndRefreshToken(memberId, refreshToken);

        if (memberRefreshToken == null) {
            throw new IllegalArgumentException("Invalid Refresh Token.");
        }

        // Access Token 또한 갱신
        Date now = new Date();
        AuthToken newAccessToken = tokenProvider.createAuthToken(
                memberId,
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

        return new TokenResponseDto(newAccessToken.getToken());
    }

    /**
     * 메일 인증 링크 전송
     * TODO: 추후 링크 hashing 및 expire 설정 기능 추가
     */
    @Override
    public void sendEmailVerificationLink() throws Exception {
        Member member = memberService.getCurrentMember();

        String emailVerificationLink = UriComponentsBuilder.fromUriString(serverUrl + "/api/v1/auth/email")
                .queryParam("memberId", member.getId())
                .build().toUriString();

        mailService.sendMailVerificationLink(member.getEmail(), emailVerificationLink);
    }

    /**
     * 메일 인증 링크 검증 및 처리 (Role Update)
     * TODO: hashing된 링크 검증 및 처리하도록 변경
     */
    @Override
    public void verifyEmailVerificationLink(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 멤버가 존재하지 않습니다."));

        member.updateRole(RoleType.USER);
        memberRepository.save(member);
    }
}
