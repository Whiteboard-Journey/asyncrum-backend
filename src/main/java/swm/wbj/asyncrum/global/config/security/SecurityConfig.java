package swm.wbj.asyncrum.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import swm.wbj.asyncrum.global.config.properties.AppProperties;
import swm.wbj.asyncrum.global.config.properties.CorsProperties;
import swm.wbj.asyncrum.global.oauth.entity.RoleType;
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
                    .antMatchers("/api/v1/**").hasAnyAuthority(RoleType.USER.getCode())
                    .antMatchers("/api/admin/v1/**").hasAnyAuthority(RoleType.ADMIN.getCode())
                    .anyRequest().authenticated()

                // OAuth 로그인 엔드포인트 관련 설정
                .and()
                    .oauth2Login()
                    .authorizationEndpoint()
                    .baseUri("/oauth2/authorization")
                    .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository())

                // OAuth 리다이렉션 엔드포인트 설정
                .and()
                    .redirectionEndpoint()
                    .baseUri("/*/oauth2/code/*")

                // OAuth 로그인 성공 이후 사용자 정보를 가져온 후 진행할 기능 설정
                .and()
                    .userInfoEndpoint()
                    .userService(customOAuth2UserService)

                // OAuth 인증 성공/실패 헨들러 설정
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
     * 쿠키 기반 인가 Repository
     * 인가 응답을 연계하고 검증할 때 사용
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
                memberRefreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository()
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
