package a.b.c.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 操作时间和日期的工具类
 *
 * @author huawei
 */
@Slf4j
public class DateTime extends Date {

  /**
   * 4年一闰，百年不润，400年又一润
   */
  public static final int LEAP_INTERVAL = 4;
  public static final int NO_LEAP_YEAR = 1000;

  /**
   * 默认情况下，时间和日期的分隔符
   */
  public static final String DATE_TIME_SEPARATOR = " ";
  /**
   * 1秒有多少ms
   */
  public static int MS_IN_SECOND = 1000;
  /**
   * 1天多少ms(86400000)
   */
  public static final long MS_IN_DAY = 1000L * 3600 * 24L;

  public enum Format {
    /**
     * 日期格式
     */
    YEAR_TO_YEAR("yyyy"),
    YEAR_TO_MONTH("yyyy-MM"),
    YEAR_TO_DAY("yyyy-MM-dd"),
    YEAR_TO_HOUR("yyyy-MM-dd HH"),
    YEAR_TO_MINUTE("yyyy-MM-dd HH:mm"),
    YEAR_TO_SECOND("yyyy-MM-dd HH:mm:ss"),
    YEAR_TO_MILLISECOND("yyyy-MM-dd HH:mm:ss.SSS"),
    MONTH_TO_MONTH("MM"),
    MONTH_TO_DAY("MM-dd"),
    MONTH_TO_HOUR("MM-dd HH"),
    MONTH_TO_MINUTE("MM-dd HH:mm"),
    MONTH_TO_SECOND("MM-dd HH:mm:ss"),
    MONTH_TO_MILLISECOND("MM-dd HH:mm:ss.SSS"),
    DAY_TO_DAY("dd"),
    DAY_TO_HOUR("dd HH"),
    DAY_TO_MINUTE("dd HH:mm"),
    DAY_TO_SECOND("dd HH:mm:ss"),
    DAY_TO_MILLISECOND("dd HH:mm:ss.SSS"),
    HOUR_TO_HOUR("HH"),
    HOUR_TO_MINUTE("HH:mm"),
    HOUR_TO_SECOND("HH:mm:ss"),
    HOUR_TO_MILLISECOND("HH:mm:ss.SSS"),
    MINUTE_TO_MINUTE("mm:ss.SSS"),
    MINUTE_TO_SECOND("mm:ss"),
    MINUTE_TO_MILLISECOND("mm:ss.SSS"),
    SECOND_TO_SECOND("ss"),
    SECOND_TO_MILLISECOND("ss.SSS"),
    MILLISECOND_TO_MILLISECOND("SSS"),
    YEAR_TO_MILLISECOND_STRING("yyyyMMddHHmmssSSS"),
    YEAR_TO_DAY_STRING("yyyyMMdd"),
    UTC8("yyyy-MM-dd'T'HH:mm:ss+08:00"),
    RFC3339("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    private String format;

    Format(String format) {
      this.format = format;
    }
  }

  private Format format;


  public boolean isLeap() {
    return isLeap(this.year());
  }

  /**
   * 判断某年是否闰年
   */
  public static boolean isLeap(int year) {
    if (year % LEAP_INTERVAL == 0 && year % NO_LEAP_YEAR != 0) {
      return true;
    }
    if (year % (LEAP_INTERVAL * NO_LEAP_YEAR) == 0) {
      return true;
    }
    return false;
  }

  public DateTime(Date date) {
    this(date.getTime());
  }

  public DateTime(long time) {
    if (time == new DateTime(time, Format.YEAR_TO_DAY).getTime()) {
      init(new Date(time), Format.YEAR_TO_DAY);
    } else {
      init(new Date(time), Format.YEAR_TO_SECOND);
    }
  }

  public DateTime(long time, Format type) {
    this(new Date(time), type);
  }

  /**
   * 通过默认格式创建时间
   *
   * @param dateString 时间串,格式必须为yyyy-MM-dd HH:mm:SS或yyyy-mm-dd
   */
  public DateTime(String dateString) {
    if (dateString.indexOf(DATE_TIME_SEPARATOR) == -1) {
      init(dateString, Format.YEAR_TO_DAY);
    } else {
      init(dateString, Format.YEAR_TO_SECOND);
    }

  }

  /**
   * 通过字符串创建时间对象
   *
   * @param dateString
   * @param type       日期格式,详见DateTime.getDateFormat
   */
  public DateTime(String dateString, Format type) {
    init(dateString, type);
  }

  /**
   * 初始化当前对象,所有创建方法最终都得调到这里
   */
  private void init(String dateTimeString, Format format) {
    try {
      SimpleDateFormat dateFormat = getDateFormat(format);
      Date date = dateFormat.parse(dateTimeString);
      this.format = format;
      super.setTime(date.getTime());
    } catch (ParseException e) {
      throw new IllegalArgumentException("unable to parse " + dateTimeString, e);
    }
  }

  public DateTime(Date date, Format format) {
    SimpleDateFormat dateFormat = getDateFormat(format);
    this.init(dateFormat.format(date), format);
  }

  private void init(Date date, Format format) {
    SimpleDateFormat dateFormat = getDateFormat(format);
    this.init(dateFormat.format(date), format);
  }

  @Override
  public String toString() {
    return toString(format);
  }

  public int year() {
    return Integer.parseInt(getDateFormat(Format.YEAR_TO_YEAR).format(this));
  }

  public int month() {
    return Integer.parseInt(getDateFormat(Format.MONTH_TO_MONTH).format(this));
  }

  public int day() {
    return Integer.parseInt(getDateFormat(Format.DAY_TO_DAY).format(this));
  }

  /**
   * 周几
   */
  public int getWeek() {
    Calendar aCalendar = Calendar.getInstance();
    aCalendar.setTime(this);
    int week = aCalendar.get(Calendar.DAY_OF_WEEK);
    if (week == 1) {
      return 7;
    } else {
      return week - 1;
    }
  }

  /**
   * 当前月份的天数
   */
  public int getDaysOfMonth() {
    Calendar time = Calendar.getInstance();
    time.clear();
    time.set(Calendar.YEAR, this.year());
    // year年
    time.set(Calendar.MONTH, this.month() - 1);
    // Calendar对象默认一月为0,month月
    int day = time.getActualMaximum(Calendar.DAY_OF_MONTH);
    return day;
  }

  public int getHour() {
    return Integer.parseInt(getDateFormat(Format.HOUR_TO_HOUR).format(this));
  }

  public int getMinute() {
    return Integer.parseInt(getDateFormat(Format.MINUTE_TO_MINUTE).format(this));
  }

  public int getSecond() {
    return Integer.parseInt(getDateFormat(Format.SECOND_TO_SECOND).format(this));
  }

  public static SimpleDateFormat getDateFormat(Format format) {
    return getFormat(format.format);
  }

  public String toString(Format format) {
    SimpleDateFormat dateFormat = getDateFormat(format);
    return dateFormat.format(this);
  }

  public String toString(String pattern) {
    SimpleDateFormat dateFormat = getFormat(pattern);
    return dateFormat.format(this);
  }

  public static String toString(long time, Format format) {
    SimpleDateFormat dateFormat = getDateFormat(format);
    return dateFormat.format(time);
  }

  public static String toString(Date time, Format format) {
    if (time == null) {
      return null;
    }
    SimpleDateFormat dateFormat = getDateFormat(format);
    return dateFormat.format(time);
  }

  public static String toStringMs(Date time) {
    if (time == null) {
      return null;
    }
    SimpleDateFormat dateFormat = getDateFormat(Format.YEAR_TO_MILLISECOND);
    return dateFormat.format(time);
  }

  public static String toString(Date time) {
    return toString(time, Format.YEAR_TO_SECOND);
  }

  public static DateTime current() {
    return new DateTime(new Date(), Format.YEAR_TO_MILLISECOND);
  }

  public static DateTime current(Format format) {
    return new DateTime(new Date(), format);
  }

  public DateTime addDay(int day) {
    DateTime dt = new DateTime(toString());
    dt.setTime(getTime() + (long) day * MS_IN_DAY);
    return dt;
  }

  public DateTime addMonth(int iMonth) {
    DateTime dt = (DateTime) clone();
    GregorianCalendar gval = new GregorianCalendar();
    gval.setTime(dt);
    gval.add(2, iMonth);
    dt.setTime(gval.getTime().getTime());
    return dt;
  }

  public DateTime addYear(int iYear) {
    DateTime dt = (DateTime) clone();
    GregorianCalendar gval = new GregorianCalendar();
    gval.setTime(dt);
    gval.add(1, iYear);
    dt.setTime(gval.getTime().getTime());
    return dt;
  }

  public DateTime addHour(int hour) {
    DateTime dt = (DateTime) clone();
    dt.setTime(getTime() + (long) hour * 3600000L);
    return dt;
  }

  public DateTime addMinute(int minute) {
    DateTime dt = (DateTime) clone();
    dt.setTime(getTime() + (long) minute * 60000L);
    return dt;
  }

  public DateTime addSecond(int second) {
    DateTime dt = (DateTime) clone();
    dt.setTime(getTime() + (long) second * 1000L);
    return dt;
  }

  public DateTime addMilliSecond(long mSecond) {
    DateTime dt = (DateTime) clone();
    dt.setTime(getTime() + mSecond);
    return dt;
  }

  public Date toDate() {
    return new Date(this.getTime());
  }

  /**
   * 每次都new一个SimpleDateFormat会占用过多内存,未每个线程造一个SimpleDateFormat
   */
  private static Map<String, ThreadLocal<SimpleDateFormat>> map = new HashMap();

  public static SimpleDateFormat getFormat(final String pattern) {
    ThreadLocal<SimpleDateFormat> t = map.get(pattern);
    if (t != null) {
      return t.get();
    }
    synchronized (DateTime.class) {
      t = map.get(pattern);
      if (t != null) {
        return t.get();
      }

      t = new ThreadLocal<SimpleDateFormat>() {
        @Override
        public SimpleDateFormat initialValue() {
          return new SimpleDateFormat(pattern);
        }
      };
      map.put(pattern, t);
      return t.get();
    }
  }
}
