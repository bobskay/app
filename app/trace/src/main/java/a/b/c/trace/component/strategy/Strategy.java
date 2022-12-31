package a.b.c.trace.component.strategy;

import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceOrder;

public interface Strategy {
    String TUN_BI_BAO = "tunBiBao";
    String WANG_GE = "wangGe";

    /**
     * 定时执行任务
     */
    void run(TaskInfo taskInfo);

    /**
     * 订单成交的回调方法
     */
    void filled(TaskInfo taskInfo, TraceOrder db);

    /**
     * 和三方同步订单数据
     */
    Object updateData(TaskInfo taskInfo);

}
