package a.b.c.base.monitor;

import a.b.c.base.monitor.vo.MonitorCountVo;
import a.b.c.base.monitor.vo.MonitorQuantityVo;
import a.b.c.base.monitor.vo.MonitorTimeVo;

/**
 * 目前系统支持的监控类型
 *
 * @author huawei
 */
public enum MonitorLogEnum {
  TIME("[time]", "\\[time\\]\\{.+\\}", MonitorTimeVo.class),
  COUNT("[count]", "\\[count\\]\\{.+\\}", MonitorCountVo.class),
  ABSOLUTE("[absolute]", "\\[absolute\\]\\{.+\\}", MonitorCountVo.class),
  QUANTITY("[quantity]", "\\[quantity\\]\\{.+\\}", MonitorQuantityVo.class);


  public final String prefix;
  public final String regex;
  public final Class voClass;

  MonitorLogEnum(String prefix, String regex, Class voClass) {
    this.prefix = prefix;
    this.regex = regex;
    this.voClass = voClass;
  }
}
