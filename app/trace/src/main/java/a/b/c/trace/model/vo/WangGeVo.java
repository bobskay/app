package a.b.c.trace.model.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class WangGeVo {
    private BigDecimal hold;
    private BigDecimal current;
    private BigDecimal high;
    private BigDecimal lastSell;
    private BigDecimal lastBuy;
    private BigDecimal quantity;
    private BigDecimal minNext;
    private BigDecimal nextBuy;
    private BigDecimal down;
    private String buyDiff;
}
