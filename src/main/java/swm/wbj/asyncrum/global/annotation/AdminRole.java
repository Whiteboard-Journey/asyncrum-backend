package swm.wbj.asyncrum.global.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 해당 API로 접근 시 Admin 권한인지 확인
 */
@Documented
@Target(ElementType.METHOD)
public @interface AdminRole {
}
