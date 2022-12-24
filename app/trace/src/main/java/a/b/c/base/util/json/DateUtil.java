package a.b.c.base.util.json;

import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
    public static final long ONE_DAY = 24 * 3600 * 1000L;
    public static final long ONE_MONTH = 31 * 24 * 3600 * 1000L;
    public static final TimeZone TIME_ZONE = TimeZone.getDefault();


    public enum DateFormat {
        YEAR_TO_SECOND("yyyy-MM-dd HH:mm:ss"),
        YEAR_TO_MILLISECOND("yyyy-MM-dd HH:mm:ss:SSS"),
        YEAR_TO_DAY("yyyy-MM-dd");


        public final String format;

        DateFormat(String format) {
            this.format = format;
        }
    }


    /**
     * 本月1号
     */
    public static Date firstOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 去年
     */
    public static Date lastYear(Date date) {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.setTime(date);
        calendar.add(Calendar.YEAR, -1);
        return calendar.getTime();
    }

    /**
     * 上个月
     */
    public static Date lastMonth(Date date) {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, -1);
        return calendar.getTime();
    }


    /**
     * 去掉时分秒
     */
    public static Date getDateStart(Date date) {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 查询时间止，因为系统查询用的是>=和<=，es存的数据又精确到毫秒，所以这个截止时间要用23:59:59:999
     */
    public static Date getDateEnd(Date date) {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * @param month 这里传的是实际月份，传到calendar里的时候会减1
     */
    public static Date newDate(int year, int month, int date) {
        Calendar calendar = Calendar.getInstance(TIME_ZONE);
        calendar.set(year, month - 1, date, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * 当天零点
     */
    public static Date today() {
        return getDateStart(new Date());
    }

    /**
     * 日期转字符串
     */
    public static String format(Date date, DateFormat dateFormat) {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat.format);
        return format.format(date);
    }

    /**
     * 将字符串转为时间，支持3种个数
     * 时间戳：1668395735408 (13位数字)
     * 只有日期：yyyy-MM-dd （10位）
     * 年月日时分秒：yyyy-MM-dd HH:mm:ss
     */
    public static Date toDate(String str) throws ParseException {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        if (str.length() == 13) {
            return new Date(Long.parseLong(str));
        }
        if (str.length() == DateFormat.YEAR_TO_DAY.format.length()) {
            SimpleDateFormat format = new SimpleDateFormat(DateFormat.YEAR_TO_DAY.format);
            return format.parse(str);
        }
        if (str.length() == DateFormat.YEAR_TO_SECOND.format.length()) {
            SimpleDateFormat format = new SimpleDateFormat(DateFormat.YEAR_TO_SECOND.format);
            return format.parse(str);
        }
        throw new RuntimeException("无法将" + str + "转为date");
    }


    /**
     * 日期转字符串
     */
    public static String format(Date date) {
        return format(date, DateFormat.YEAR_TO_MILLISECOND);
    }


    public static String format(Long time) {
        return format(new Date(time), DateFormat.YEAR_TO_SECOND);
    }

}
