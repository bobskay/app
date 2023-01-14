package a.b.c.trace.model.vo;

import a.b.c.base.util.DateTime;
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

    /**
     * 总耗时
     * */
    public String getConsumerStr(){
        if(this.relatedOrder==null){
            return null;
        }
        return showTime(this.getSellEnd(),this.getBuyStart());
    }

    /**
     * 买入耗时
     * */
    public String getBuyConsumer(){
        return showTime(this.getBuyStart(),this.getByEnd());
    }

    /**
     * 卖出耗时
     * */
    public String getSellConsumer(){
        return showTime(this.getSellEnd(),this.getSellStart());
    }

    private String showTime(Date start, Date end){
        if(start==null){
            return "-";
        }
        if(end==null){
            return  DateTime.showHourTime(System.currentTimeMillis()-start.getTime());
        }
        return  DateTime.showHourTime(end.getTime()-start.getTime());
    }
}
