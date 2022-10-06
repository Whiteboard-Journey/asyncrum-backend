package swm.wbj.asyncrum.global.oauth.utils;

import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
public class TokenUtil {

    /**
     * TokenAuthenticationFilter 에서 SecurityContext 에 저장한 Authentication -> 유저 정보 (member id) 꺼냄
     */
    public static Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(validateAuthentication(authentication)) {
            throw new IllegalArgumentException("Security Context 에 인증정보가 없거나 잘못되었습니다.");
        }

        return Long.parseLong(authentication.getName());
    }

    private static boolean validateAuthentication(Authentication authentication) {
        return authentication == null || authentication.getName() == null;
    }
}
