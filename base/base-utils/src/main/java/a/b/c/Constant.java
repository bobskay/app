package a.b.c;

import a.b.c.base.annotation.Remark;

public class Constant {

  @Remark("map默认容量")
  public static final Integer DEFAULT_CAPACITY = 16;

  @Remark("环境相关配置文件所在目录")
  public static final String CONFIG_DIR = "/opt/config";

  @Remark("默认每页显示条数")
  public static Integer DEFAULT_PAGE_SIZE = 10;

  @Remark("表名前缀")
  public static String TABLE_PIX = "";

  @Remark("是否是开发环境，很多地方根据这个判断是否打印日志")
  public static boolean DEBUG = true;
}
