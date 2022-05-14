package a.b.c.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 给字段,方法或类加备注,因为编译后注释会去掉
 *
 * @author
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Remark {
  String value() default "";
}
