package a.b.c.trace.model.vo;

import a.b.c.base.util.DateTime;
import a.b.c.exchange.dto.OpenOrder;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OpenOrderVo extends OpenOrder {

    /**
     * 持有时长
     * */
    private Long holdSecond;

    /**
     * 基准价格
     * */
    private BigDecimal basePrice;

    /**
     * 期望价格
     * */
    private BigDecimal expectPrice;

    private String expectPriceDesc;

    public String getHoldTime(){
        if(holdSecond==null){
            return "0";
        }
        return DateTime.showTime(holdSecond*1000);
    }

}
