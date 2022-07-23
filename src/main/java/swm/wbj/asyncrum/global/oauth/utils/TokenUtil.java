package swm.wbj.asyncrum.global.oauth.utils;

import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
public class TokenUtil {

    /**
     * TokenAuthenticationFilter에서 SecurityContext에 저장한 Authentication -> 유저 정보 (member id) 꺼냄
     */
    public static Long getCurrentMemberId() {
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null || authentication.getName() == null) {
            throw new IllegalArgumentException("Security Context에 인증정보가 없습니다.");
        }

        return Long.parseLong(authentication.getName());
    }
}
