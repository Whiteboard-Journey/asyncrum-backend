package swm.wbj.asyncrum.global.config.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import swm.wbj.asyncrum.global.oauth.token.TokenProvider;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public TokenProvider jwtProvider() {
        return new TokenProvider(secret);
    }
}
