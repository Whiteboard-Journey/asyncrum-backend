package swm.wbj.asyncrum.domain.auth;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import swm.wbj.asyncrum.domain.auth.dto.LoginRequestDto;
import swm.wbj.asyncrum.domain.auth.dto.TokenResponseDto;
import swm.wbj.asyncrum.domain.member.entity.Member;
import swm.wbj.asyncrum.domain.member.entity.MemberRefreshToken;
import swm.wbj.asyncrum.domain.member.repository.MemberRefreshTokenRepository;
import swm.wbj.asyncrum.domain.member.repository.MemberRepository;
import swm.wbj.asyncrum.global.oauth.entity.UserPrincipal;
import swm.wbj.asyncrum.global.oauth.token.AuthToken;
import swm.wbj.asyncrum.global.oauth.token.TokenProvider;
import swm.wbj.asyncrum.global.properties.AppProperties;
import swm.wbj.asyncrum.global.type.RoleType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Collection;
import java.util.Date;
import java.util.Optional;

class AuthServiceUnitTest {

    AppProperties appProperties = Mockito.mock(AppProperties.class);
    TokenProvider tokenProvider = Mockito.mock(TokenProvider.class);
    AuthenticationManager authenticationManager = Mockito.mock(AuthenticationManager.class);
    MemberRefreshTokenRepository memberRefreshTokenRepository = Mockito.mock(MemberRefreshTokenRepository.class);
    MemberRepository memberRepository = Mockito.mock(MemberRepository.class);

    @InjectMocks
    AuthService authService = new AuthServiceImpl(appProperties, tokenProvider, authenticationManager, memberRefreshTokenRepository, memberRepository);

    static final Long MOCK_ID = 1L;

    @BeforeEach
    void setup() {
        AppProperties.Auth auth = new AppProperties.Auth("secret", 10L, 10L);

        Mockito.when(appProperties.getAuth()).thenReturn(auth);
    }

    @Test
    void localLogin() {
        Member member = new Member() {
            @Override
            public Long getId() {
                return MOCK_ID;
            }

            @Override
            public RoleType getRoleType() {
                return RoleType.USER;
            }
        };
        AuthToken token = new AuthToken(null, null) {
            @Override
            public String getToken() {
                return "token";
            }
        };

        String email = "email@email.com";
        LoginRequestDto requestDto = new LoginRequestDto(); requestDto.setEmail(email);
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        Mockito.when(authenticationManager.authenticate(Mockito.any())).thenReturn(new Authentication() {

            @Override
            public String getName() {
                return null;
            }

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return null;
            }

            @Override
            public Object getCredentials() {
                return null;
            }

            @Override
            public Object getDetails() {
                return null;
            }

            @Override
            public Object getPrincipal() {
                return UserPrincipal.create(member);
            }

            @Override
            public boolean isAuthenticated() {
                return false;
            }

            @Override
            public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

            }
        });
        Mockito.when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        Mockito.when(tokenProvider.createAuthToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(Date.class))).thenReturn(token);
        Mockito.when(tokenProvider.createAuthToken(Mockito.anyString(), Mockito.any(Date.class))).thenReturn(token);

        TokenResponseDto responseDto = authService.localLogin(request, response, requestDto);

        Mockito.verify(authenticationManager).authenticate(Mockito.any());
        // SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.verify(tokenProvider).createAuthToken(Mockito.anyString(), Mockito.anyString(), Mockito.any(Date.class));
        Mockito.verify(tokenProvider).createAuthToken(Mockito.anyString(), Mockito.any(Date.class));
        Mockito.verify(memberRefreshTokenRepository).saveAndFlush(Mockito.any(MemberRefreshToken.class));
        // CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        // CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);
        Assertions.assertEquals(responseDto.getToken(), token.getToken());
    }

    @Test
    void refreshToken() {
        AuthToken token = Mockito.mock(AuthToken.class);
        Mockito.when(token.getToken()).thenReturn("token");

        Claims claims = Mockito.mock(Claims.class);
        Mockito.when(claims.getExpiration()).thenReturn(new Date());
        Mockito.when(claims.get("role", String.class)).thenReturn("ROLE_USER");

        MemberRefreshToken memberRefreshToken = Mockito.mock(MemberRefreshToken.class);

        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

        AuthToken mockAuthToken = Mockito.mock(AuthToken.class);
        Mockito.when(mockAuthToken.validateToken()).thenReturn(true);

        Mockito.when(tokenProvider.convertAuthToken(Mockito.any())).thenReturn(mockAuthToken);
        Mockito.when(mockAuthToken.validateToken()).thenReturn(true);
        Mockito.when(mockAuthToken.getExpiredTokenClaims()).thenReturn(claims);
        Mockito.when(mockAuthToken.getTokenClaims()).thenReturn(claims);
        Mockito.when(memberRefreshTokenRepository.findByMemberIdAndRefreshToken(Mockito.any(), Mockito.any())).thenReturn(memberRefreshToken);
        Mockito.when(tokenProvider.createAuthToken(Mockito.any(), Mockito.any(), Mockito.any(Date.class))).thenReturn(token);
        Mockito.when(tokenProvider.createAuthToken(Mockito.anyString(), Mockito.any(Date.class))).thenReturn(token);

        TokenResponseDto responseDto = authService.refreshToken(request, response);

        Mockito.verify(tokenProvider, Mockito.times(2)).convertAuthToken(Mockito.any());
        Mockito.verify(mockAuthToken, Mockito.times(1)).validateToken();
        Mockito.verify(mockAuthToken).getExpiredTokenClaims();
        Mockito.verify(memberRefreshTokenRepository).findByMemberIdAndRefreshToken(Mockito.any(), Mockito.any());
        Mockito.verify(tokenProvider).createAuthToken(Mockito.any(), Mockito.any(), Mockito.any(Date.class));
        Mockito.verify(tokenProvider).createAuthToken(Mockito.any(), Mockito.any(Date.class));
        Mockito.verify(memberRefreshToken).setRefreshToken(Mockito.any());
        // CookieUtil.deleteCookie(request, response, REFRESH_TOKEN);
        // CookieUtil.addCookie(response, REFRESH_TOKEN, refreshToken.getToken(), cookieMaxAge);
        Assertions.assertEquals(responseDto.getToken(), token.getToken());
    }
}