package swm.wbj.asyncrum.global.async;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        System.out.println("--- THREAD ERROR ---");
        System.out.println("Exception Message: " + ex.getMessage());
        System.out.println("Exception Method: " + method.getName());
        for(Object param : params) {
            System.out.println("Exception Parameter Values: " + param);
        }
    }
}
