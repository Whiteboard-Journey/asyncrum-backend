package swm.wbj.asyncrum.global.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("--- THREAD ERROR ---");
        log.error("Exception Message: " + ex.getMessage());
        log.error("Exception Method: " + method.getName());
        for(Object param : params) {
            log.error("Exception Parameter Values: " + param);
        }
    }
}
