package a.b.c.base.enums;

public interface Regex {
  /**
   * 常用字符,字母数字下划线
   */
  public static final String REG_COMMON = "^([_]|[a-z]|[A-Z]|[0-9])*$";
  /**
   * 非法字符
   */
  public static final String REG_INVALID_WORD = ".*[\\\\<>\"=\\';].*";
  /**
   * html标签
   */
  public static final String REG_HTML_TAG = "<([^<]|[^>]*)>";
  /**
   * 匹配一行
   */
  public static final String REG_LINE = "[\\S ]+[\\S| ]*";
  /**
   * 匹配非空的一行
   */
  public static final String REG_LLINETRIM = "[\\S| ]+";
  /**
   * 匹配空白
   */
  public static final String REG_BLANK = "\\s+";
  /**
   * 匹配整形
   */
  public static final String REG_INTEGER = "\\d+";
  /**
   * java注释
   */
  public static final String REG_JAVACOMMENT = "/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/";
  /**
   * 字母
   */
  public static final String REG_LETTER = "([a-z]|[A-Z])*";
  /**
   * 数字字母或者下划线 ,以字母开头(java命名规范)
   */
  public static final String REG_NAMING_CONVENTION = "^([a-z]|[A-Z])+([\\_]|\\d|[a-z]|[A-Z])*$";
  /**
   * 匹配数字
   */
  public static final String REG_NUMBER = "^\\d*[.]?\\d+$";
  /**
   * 字母数字或汉字
   */
  public static final String REG_WORD = "^([\u4e00-\u9fa5]|[a-z]|[A-Z]|[0-9])*$";
  /**
   * email
   */
  public static final String REG_EMAIL = "^(\\w)+(\\.\\w+)*@(\\w)+((\\.\\w+)+)$";

  public static final String REG_CHINESE="[\u4e00-\u9fa5]";
}
