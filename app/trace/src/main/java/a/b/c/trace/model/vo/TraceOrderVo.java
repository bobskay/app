package a.b.c.trace.model.vo;

import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceOrder;
import lombok.Data;

@Data
public class TraceOrderVo extends TraceOrder {
    private String businessType;
    private TaskInfo taskInfo;
    //关联订单,网格交易时买入后会立即卖出
    private TraceOrder relatedOrder;
}
