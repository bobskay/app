package a.b.c.base.monitor;

import a.b.c.base.monitor.vo.MonitorCountVo;
import a.b.c.base.monitor.vo.MonitorQuantityVo;
import a.b.c.base.monitor.vo.MonitorTimeVo;
import a.b.c.base.utils.StringUtil;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huawei
 */
@Slf4j
public class MonitorLogApi {


    /**
     * 生成某个请求耗时的日志
     *
     * @param name 请求名称
     * @param time 耗时
     */
    public static String toTimeString(String name, int time) {
        StringBuilder sb = new StringBuilder();
        sb.append(MonitorLogEnum.TIME.prefix);
        sb.append("{name:'").append(name).append("',time:").append(time).append("}");
        return sb.toString();
    }

    /**
     * 生成某个数量的统计
     *
     * @param name  名称
     * @param count 数量
     * @param stats 统计粒度，用户逗号隔开的数字
     */
    public static String toQuantityString(String name, int count, String stats) {
        StringBuilder sb = new StringBuilder();
        sb.append(MonitorLogEnum.QUANTITY.prefix);
        sb.append("{name:'").append(name)
                .append("',count:").append(count)
                .append(",stats:'").append(stats)
                .append("'}");
        return sb.toString();
    }

    /**
     * 生成某个数量的统计
     *
     * @param message 日志内容：[quantity]{name:'abc',count:111,stats:'111'}
     */
    public static MonitorQuantityVo toQuantityVo(String message) {
        return toBean(message, MonitorLogEnum.QUANTITY);
    }

    public static String toAbsoluteString(String name, long count) {
        StringBuilder sb = new StringBuilder();
        sb.append(MonitorLogEnum.ABSOLUTE.prefix);
        sb.append("{name:'").append(name).append("',count:'").append(count).append("'}");
        return sb.toString();
    }

    /**
     * 生成监控次数的日志
     *
     * @param name  请求名称
     * @param count 请求次数
     */
    public static String toCountString(String name, Integer count) {
        StringBuilder sb = new StringBuilder();
        sb.append(MonitorLogEnum.COUNT.prefix);
        if (count == null || count == 1) {
            sb.append("{name:'").append(name).append("'}");
            return sb.toString();
        }
        sb.append("{name:'").append(name).append("',count:").append(count).append("}");
        return sb.toString();
    }

    public static String toCountString(String name) {
        return toCountString(name, 1);
    }

    /**
     *
     */
    public static String toTimeString(String name, long time) {
        return toTimeString(name, (int) time);
    }

    /**
     * 从日志里提取耗时统计
     */
    public static MonitorTimeVo toTimeVo(String message) {
        return toBean(message, MonitorLogEnum.TIME);
    }

    /**
     * 从日志里提取次数统计
     */
    public static MonitorCountVo toCountVo(String message) {
        return toBean(message, MonitorLogEnum.COUNT);
    }

    /**
     * 从日志里提取实际值
     */
    public static MonitorCountVo toAbsoluteVo(String message) {
        return toBean(message, MonitorLogEnum.ABSOLUTE);
    }

    public static <T> T toBean(String message, MonitorLogEnum enu) {
        String json = StringUtil.pickUpFirst(message, enu.regex);
        if (json == null) {
            return null;
        }
        json = json.substring(enu.prefix.length());
        return (T) JSONObject.parseObject(json, enu.voClass);
    }

    /**
     * 生成监控名称，名称格式为 name:tag1,tag2,tag3
     */
    public static String createName(String name, String... tags) {
        if (tags == null || tags.length == 0) {
            return name;
        }

        StringBuilder sb = new StringBuilder();
        for (String tag : tags) {
            for (String s : tag.split(",")) {
                if (StringUtil.isEmpty(s)) {
                    continue;
                }
                sb.append(s).append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        return name + ":" + sb.toString();
    }

    public static String getName(String nameWithTag) {
        int flag = nameWithTag.indexOf(":");
        if (flag == -1) {
            return nameWithTag;
        }
        return nameWithTag.substring(0, flag);
    }

    public static String getTags(String nameWithTag) {
        int flag = nameWithTag.indexOf(":");
        if (flag == -1) {
            return "";
        }
        return nameWithTag.substring(flag + 1);
    }

}
