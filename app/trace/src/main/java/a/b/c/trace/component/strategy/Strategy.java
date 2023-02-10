package a.b.c.trace.component.strategy;

import a.b.c.trace.component.strategy.vo.StrategyData;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceOrder;

public interface Strategy<T extends StrategyData> {
    String TUN_BI_BAO = "tunBiBao";
    String WANG_GE = "wangGe";
    String CLIMB = "climb";

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
    T updateData(TaskInfo taskInfo);

}
