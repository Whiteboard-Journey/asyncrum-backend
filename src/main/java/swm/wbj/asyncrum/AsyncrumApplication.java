package swm.wbj.asyncrum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import swm.wbj.asyncrum.global.config.properties.AppProperties;
import swm.wbj.asyncrum.global.config.properties.CorsProperties;

@EnableConfigurationProperties({
		CorsProperties.class,
		AppProperties.class
})
@EnableJpaAuditing
@SpringBootApplication
public class AsyncrumApplication {

	public static void main(String[] args) {
		SpringApplication.run(AsyncrumApplication.class, args);
	}

}
