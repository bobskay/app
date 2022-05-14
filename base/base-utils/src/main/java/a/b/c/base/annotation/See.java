package a.b.c.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface See {

  @Remark("关联的类")
  Class[] value() default {};

  @Remark("字段对应的枚举")
  Class enums() default Object.class;

  @Remark("model类对应的表")
  String[] tables() default {};


}
