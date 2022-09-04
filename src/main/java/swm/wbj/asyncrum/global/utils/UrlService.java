package swm.wbj.asyncrum.global.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class UrlService {

    @Value("${server.url}")
    private String serverUrl;

    public String buildURL(String apiUrl, String param, Object object) {
        return UriComponentsBuilder.fromUriString(serverUrl + apiUrl)
                .queryParam(param, object)
                .build().toUriString();
    }
}
