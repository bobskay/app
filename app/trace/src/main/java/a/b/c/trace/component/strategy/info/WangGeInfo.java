package a.b.c.trace.component.strategy.info;

import a.b.c.base.util.DateTime;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.Kline;
import a.b.c.exchange.enums.CandlestickInterval;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.trace.component.strategy.vo.WangGeData;
import a.b.c.trace.model.TraceOrder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
public class WangGeInfo {
    public static final Long MIN_INTERVAL= TimeUnit.MINUTES.toMillis(30);
    private Long taskId;
    private TraceOrder lastFilled;
    public WangGeInfo(Long taskId){
        this.taskId=taskId;
    }


    public void lastFilled(TraceOrder lastFilled) {
        lastFilled.setUpdatedAt(new Date());
        this.lastFilled=lastFilled;
    }

    /**
     *
     * */
    public boolean isBuyable() {
        if(lastFilled==null){
            return true;
        }
        if (OrderSide.SELL.toString().equalsIgnoreCase(lastFilled.getOrderSide().toString())) {
            return true;
        }
        long duration = System.currentTimeMillis() - lastFilled.getUpdatedAt().getTime();
        if (duration < MIN_INTERVAL) {
            log.info("连续买入过于频繁，需等待"+ DateTime.showHourTime(MIN_INTERVAL-duration));
            return false;
        }
        return true;
    }

}
