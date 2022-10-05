package swm.wbj.asyncrum.global.oauth.token;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Bean
    public TokenProvider jwtProvider() {
        return new TokenProvider(secret);
    }
}
