package a.b.c.base.utils;

import ch.qos.logback.classic.Level;
import java.io.PrintWriter;
import java.io.StringWriter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * @author huawei
 */
@Slf4j
public class LogUtil {

  /**
   * 堆栈的调用方起始位置
   */
  public static final int CALL_LENGTH = 2;
  public static final String REQUEST_ID = "requestId";

  /**
   * 获取原始异常
   */
  public static Throwable getCause(Throwable ex) {
    while (true) {
      if (ex.getCause() == null) {
        return ex;
      } else {
        ex = ex.getCause();
      }
    }
  }

  /**
   * 高亮显示打印信息，在eclipse里可以直接跳转
   */
  public static void debug(Object msg) {
    RuntimeException ex = new RuntimeException(msg + "");
    StringWriter writer = new StringWriter();
    ex.printStackTrace(new PrintWriter(writer));
    String text = writer.toString();
    int first = text.indexOf(StringUtil.line());
    int second = text.indexOf(StringUtil.line(), first + 1);
    int third = text.indexOf(StringUtil.line(), second + 1);
    text = text.substring(0, first) + text.substring(second, third);
    log.error(StringUtil.line() + text);
  }

  /**
   * 打印调用者信息
   */
  public static void debugCaller(Exception ex) {
    StackTraceElement[] st = ex.getStackTrace();
    StackTraceElement m = st[1];
    if (st.length > CALL_LENGTH) {
      m = st[CALL_LENGTH];
    }
    String className = m.getClassName();
    int pos = className.lastIndexOf('.');
    className = className.substring(pos);
    log.debug(
        "" + ex.getMessage() + " at " + className + "." + m.getMethodName() + "(" + m.getFileName()
            + ":" + m.getLineNumber() + ")");
  }

  /**
   * 打印调用者
   */
  public static void debugCaller(Object msg) {
    RuntimeException ex = new RuntimeException(msg + "");
    StackTraceElement[] st = ex.getStackTrace();
    StackTraceElement m = st[1];
    if (st.length > CALL_LENGTH) {
      m = st[CALL_LENGTH];
    }
    String className = m.getClassName();
    int pos = className.lastIndexOf('.');
    className = className.substring(pos);
    log.debug(
        "" + msg + " at " + className + "." + m.getMethodName() + "(" + m.getFileName() + ":" + m
            .getLineNumber() + ")");
  }

  public static void changeLevel(Class<?> clazz, Level level) {
    LogLevel.changeLevel(clazz, level);
  }

  /**
   * 获得调用者信息
   */
  public static String getCalller() {
    RuntimeException ex = new RuntimeException();
    StackTraceElement[] st = ex.getStackTrace();
    StackTraceElement m = st[1];
    if (st.length > CALL_LENGTH) {
      m = st[CALL_LENGTH];
    }
    String className = m.getClassName();
    int pos = className.lastIndexOf('.');
    className = className.substring(pos + 1);
    return className + "." + m.getMethodName() + "(" + m.getFileName() + ":" + m.getLineNumber()
        + ")";
  }

  /**
   * 将异常转为文本
   */
  public static String getExceptionText(Throwable throwable) {
    if (throwable == null) {
      return "";
    }
    StringWriter writer = new StringWriter();
    throwable.printStackTrace(new PrintWriter(writer));
    return writer.toString();
  }


  public static void setRequestId(String requestId) {
    MDC.put(REQUEST_ID, requestId);
  }

  public static void appendRequestId(String flag) {
    String req = MDC.get(REQUEST_ID);
    MDC.put(REQUEST_ID, req + "-" + flag);
  }


  public static class LogLevel {

    public static void trace(Class<?> clazz) {
      changeLevel(clazz, Level.TRACE);
    }

    public static void debug(Class<?> clazz) {
      changeLevel(clazz, Level.DEBUG);
    }

    public static void info(Class<?> clazz) {
      changeLevel(clazz, Level.INFO);
    }

    public static void changeLevel(Class<?> clazz, Level level) {
      try {
        Logger logger = LoggerFactory.getLogger(clazz);
        ch.qos.logback.classic.Logger log = (ch.qos.logback.classic.Logger) logger;
        log.setLevel(level);
      } catch (Exception ex) {
        System.out.println("修改日志级别出错,要修改的类:" + clazz);
        ex.printStackTrace();
      }
    }
  }

}
