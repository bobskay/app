package a.b.c.trace.model.vo;

import a.b.c.base.util.DateTime;
import a.b.c.base.util.json.DateUtil;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceOrder;
import lombok.Data;

import java.util.Date;

@Data
public class TraceOrderVo extends TraceOrder {
    private String businessType;
    private TaskInfo taskInfo;
    //关联订单,网格交易时买入后会立即卖出
    private TraceOrder relatedOrder;


    public Date getBuyStart() {
        return this.getCreatedAt();
    }

    public Date getByEnd() {
        return this.getFinishAt();
    }

    public Date getSellStart() {
        if (this.relatedOrder != null) {
            return this.relatedOrder.getCreatedAt();
        }
        return null;
    }

    public Date getSellEnd() {
        if (this.relatedOrder != null) {
            return this.relatedOrder.getFinishAt();
        }
        return null;
    }

    public Long getConsumer() {
        if(this.relatedOrder==null){
            return null;
        }
        if (this.getSellEnd() != null) {
            return this.getSellEnd().getTime() - this.getBuyStart().getTime();
        }
        return System.currentTimeMillis()-this.getBuyStart().getTime();
    }

    public String getConsumerStr(){
        return DateTime.showTime(getConsumer());
    }
}
