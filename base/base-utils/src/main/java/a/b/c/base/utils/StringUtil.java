package a.b.c.base.utils;

import cn.hutool.core.util.StrUtil;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StringUtil {

  public static final int LOWER_A = 'a';
  public static final int LOWER_Z = 'z';
  public static final int A = 'A';
  public static final int Z = 'Z';
  /**
   * 大小写转换要增加或减少的值
   * */
  public static final  int CASE_SWITCH=32;

  public static String line() {
    return "\n";
  }

  public static boolean isEmpty(Object str) {
    if(str instanceof String){
      return StrUtil.isEmpty((String)str);
    }
    return str==null;
  }


  /**
   * 将首字母转为大写，其他不变
   */
  public static String firstUp(String str) {
    char[] ch;
    ch = str.toCharArray();
    if (ch[0] >= LOWER_A && ch[0] <= LOWER_Z) {
      ch[0] = (char) (ch[0] - CASE_SWITCH);
    }
    String newString = new String(ch);
    return newString;
  }

  /**
   * 将首字母转为小写，其他不变
   */
  public static String firstLower(String str) {
    String first = str.substring(0, 1);
    String orther = str.substring(1);
    return first.toLowerCase() + orther;
  }

  /**
   * 按照大写分割,并且用split连接
   */
  public static String camelConvert(String str, String split) {
    char[] ch = str.toCharArray();
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < ch.length; i++) {
      if (ch[i] >= 'A' && ch[i] <= 'Z') {
        ch[i] = (char) (ch[i] + CASE_SWITCH);
        if (sb.length() > 0) {
          sb.append(split);
        }
        sb.append(ch[i]);
        continue;
      }
      sb.append(ch[i]);
    }
    return sb.toString();
  }

  /**
   * 提取符合正则表达式的字符串
   */
  public static String pickUpFirst(String str, String regx) {
    if (regx == null) {
      log.error("传入的正则为null", new Exception());
      return null;
    }
    // 忽略大小写.包括换行
    regx = "(?s)(?i)" + regx;
    Pattern pattern = Pattern.compile(regx);
    Matcher matcher = pattern.matcher(str);
    if (matcher.find()) {
      return matcher.group();
    }
    return null;
  }

  public static boolean isNotEmpty(String str) {
    return !isEmpty(str);
  }

  public static String pickAround(String typeName, String startFlag, String endFlag) {
    int st=typeName.indexOf(startFlag);
    if(st==-1){
      return null;
    }
    int end=typeName.indexOf(endFlag,st);
    if(end==-1){
      return null;
    }
    return typeName.substring(st+startFlag.length(),end);
  }
}
