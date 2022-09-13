package swm.wbj.asyncrum.global.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import swm.wbj.asyncrum.global.exception.OperationNotAllowedException;
import swm.wbj.asyncrum.global.oauth.token.AuthToken;
import swm.wbj.asyncrum.global.oauth.token.TokenProvider;
import swm.wbj.asyncrum.global.oauth.utils.HeaderUtil;
import swm.wbj.asyncrum.global.type.RoleType;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
@Aspect
public class RoleAspect {

    private final TokenProvider tokenProvider;

    @Before("@annotation(swm.wbj.asyncrum.global.annotation.AdminRole)")
    public void checkAdminRole() {
        String currentRole = getCurrentRole();

        if(!hasAdminRole(currentRole)) {
            throw new OperationNotAllowedException();
        }
    }

    @Before("@annotation(swm.wbj.asyncrum.global.annotation.UserRole)")
    public void checkUserRole() {
        String currentRole = getCurrentRole();

        if(!(hasUserRole(currentRole) || hasAdminRole(currentRole))) {
            throw new OperationNotAllowedException();
        }
    }

    private boolean hasAdminRole(String currentRole) {
        return RoleType.of(currentRole).equals(RoleType.ADMIN);
    }

    private boolean hasUserRole(String currentRole) {
        return RoleType.of(currentRole).equals(RoleType.USER);
    }

    private String getCurrentRole() {
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();

        String tokenStr = HeaderUtil.getAccessToken(request);
        AuthToken token = tokenProvider.convertAuthToken(tokenStr);
        return token.getPayloads(AuthToken.AUTHORITIES_KEY);
    }
}
