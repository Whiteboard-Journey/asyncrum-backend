package swm.wbj.asyncrum.global.oauth.utils;

import javax.servlet.http.HttpServletRequest;

/**
 * // HTTP Request 헤더에서 Authorization: Bearer 토큰 가져오는 유틸리티 클래스
 */
public class HeaderUtil {

    private final static String HEADER_AUTHORIZATION = "Authorization";
    private final static String TOKEN_PREFIX = "Bearer ";

    public static String getAccessToken(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_AUTHORIZATION);

        if (headerValue == null) {
            return null;
        }

        if (headerValue.startsWith(TOKEN_PREFIX)) {
            return headerValue.substring(TOKEN_PREFIX.length());
        }

        return null;
    }
}
