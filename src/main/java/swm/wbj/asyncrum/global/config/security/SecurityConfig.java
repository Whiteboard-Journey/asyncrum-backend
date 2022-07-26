package swm.wbj.asyncrum.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRefreshTokenRepository;
import swm.wbj.asyncrum.domain.userteam.member.repository.MemberRepository;
import swm.wbj.asyncrum.global.config.properties.AppProperties;
import swm.wbj.asyncrum.global.config.properties.CorsProperties;
import swm.wbj.asyncrum.domain.userteam.member.entity.RoleType;
import swm.wbj.asyncrum.global.oauth.exception.RestAuthenticationEntryPoint;
import swm.wbj.asyncrum.global.oauth.filter.TokenAuthenticationFilter;
import swm.wbj.asyncrum.global.oauth.handler.OAuth2AuthenticationFailureHandler;
import swm.wbj.asyncrum.global.oauth.handler.OAuth2AuthenticationSuccessHandler;
import swm.wbj.asyncrum.global.oauth.handler.TokenAccessDeniedHandler;
import swm.wbj.asyncrum.global.oauth.repository.OAuth2AuthorizationRequestBasedOnCookieRepository;
import swm.wbj.asyncrum.global.oauth.service.CustomOAuth2UserService;
import swm.wbj.asyncrum.global.oauth.service.CustomUserDetailService;
import swm.wbj.asyncrum.global.oauth.token.TokenProvider;

import java.util.Arrays;

@RequiredArgsConstructor
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsProperties corsProperties;
    private final AppProperties appProperties;
    private final TokenProvider tokenProvider;
    private final CustomUserDetailService customUserDetailService;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final TokenAccessDeniedHandler tokenAccessDeniedHandler;
    private final MemberRefreshTokenRepository memberRefreshTokenRepository;
    private final MemberRepository memberRepository;

    /**
     * 시큐리티 전체 설정
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // CORS (필터) 설정
                    .cors()

                // 세션 비활성화 (HTTP 무상태) 설정
                .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // 인증 진입점 (예외처리) 설정
                .and()
                    .csrf().disable()
                    .formLogin().disable()
                    .httpBasic().disable()
                    .exceptionHandling()
                    .authenticationEntryPoint(new RestAuthenticationEntryPoint())
                    .accessDeniedHandler(tokenAccessDeniedHandler)

                // 요청 경로별(URL) 인증 관련 설정
                .and()
                    .authorizeRequests()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                    .antMatchers(HttpMethod.POST, "/api/v1/members").permitAll()
                    .antMatchers("/api/v1/auth/*").permitAll()
                    .antMatchers("/api/v1/**").hasAnyAuthority(RoleType.USER.getCode())
                    .antMatchers("/api/admin/v1/**").hasAnyAuthority(RoleType.ADMIN.getCode())
                    .anyRequest().authenticated()

                // 1. OAuth 로그인 요청 시 사용하는 엔드포인트 관련 설정
                //    authorizationRequestRepository를 통해 OAuth Request 상태를 저장
                .and()
                    .oauth2Login()
                    .authorizationEndpoint()
                    .baseUri("/oauth2/authorization")
                    .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())

                // 2. OAuth 로그인 후 다시 백엔드 서버로 Authorization Code를 받기 위한 리다이렉션 엔드포인트 설정
                //    이후 백엔드 Spring OAuth가 자동으로 Authorization Code -> Access Token -> 사용자 정보를 가져옴
                .and()
                    .redirectionEndpoint()
                    .baseUri("/*/oauth2/code/*")

                // 3. Access Token으로 Resource Server에서 사용자 정보를 가져온 후 진행할 추가 로직 설정
                //    customOAuth2UserService에서 사용자 정보를 기존 계정(Memeber)과 연계하여 처리(회원가입, 갱신 등)
                .and()
                    .userInfoEndpoint()
                    .userService(customOAuth2UserService)

                // 4. OAuth 인증 성공/실패 헨들러 설정
                //    OAuth 일련의 과정이 끝나면 oAuth2AuthenticationSuccessHandler 호출,
                //    OAuth 과정 중간에 실패시 oAuth2AuthenticationFailureHandler 호출
                .and()
                    .successHandler(oAuth2AuthenticationSuccessHandler())
                    .failureHandler(oAuth2AuthenticationFailureHandler());

        // 기존 스프링 사용자 인증 필터 앞에 토큰 인증 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    /**
     * UserDetailsService 설정
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailService)
                .passwordEncoder(passwordEncoder());
    }

    /**
     * 시큐리티 설정 시, 사용할 인코터 설정
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * AuthenticationManager 설정
     */
    @Override
    @Bean(BeanIds.AUTHENTICATION_MANAGER)
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    /**
     * 토큰 필터 설정
     */
    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter(tokenProvider);
    }

    /**
     * 쿠키 기반 Authorization Request Repository
     * Authorization Request를 연계하고 검증할 때 사용
     */
    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }

    /**
     * OAuth 인증 성공 핸들러
     */
    @Bean
    public OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(
                tokenProvider,
                appProperties,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),
                memberRefreshTokenRepository,
                memberRepository
        );
    }

    /**
     * OAuth 인증 실패 핸들러
     */
    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler(oAuth2AuthorizationRequestBasedOnCookieRepository());
    }

    /**
     * CORS 설정
     */
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource corsConfigSource = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedHeaders(Arrays.asList(corsProperties.getAllowedHeaders().split(",")));
        corsConfig.setAllowedMethods(Arrays.asList(corsProperties.getAllowedMethods().split(",")));
        corsConfig.setAllowedOrigins(Arrays.asList(corsProperties.getAllowedOrigins().split(",")));
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(corsConfig.getMaxAge());

        corsConfigSource.registerCorsConfiguration("/**", corsConfig);
        return corsConfigSource;
    }

}
