package a.b.c.base.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 字段配置
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {

  @Remark("长度")
  int length() default 0;

  @Remark("数据库类型")
  String type() default "";

  @Remark("是否无符类型")
  boolean unsigned() default false;

  @Remark("默认值")
  String defaultValue() default "";

  @Remark("是否是索引字段")
  boolean key() default false;

  @Remark("是否唯一")
  boolean unique() default false;

  @Remark("是否可以为空")
  boolean nullable() default false;

  @Remark("是否是主键")
  boolean pk() default  false;

  @Remark("小数位数")
  int fraction() default 0;

  @Remark("额外的配置项，例如:on update CURRENT_TIMESTAMP,auto_increment")
  String extra() default "";

}
