package swm.wbj.asyncrum.global.config.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration Properties 안의 Cors 관련 환경설정 값들을 가져와 Cors Configuration 처리
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {

    private String allowedOrigins;
    private String allowedMethods;
    private String allowedHeaders;
    private Long maxAge;

}
