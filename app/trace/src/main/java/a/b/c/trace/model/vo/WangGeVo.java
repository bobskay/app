package a.b.c.trace.model.vo;

import a.b.c.transaction.cache.ConfigInfo;
import a.b.c.transaction.cache.RunInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class WangGeVo {
    private BigDecimal hold;
    private BigDecimal current;
    private BigDecimal high;
    private String buyInterval;
    private BigDecimal lastSell;
    private BigDecimal lastBuy;
    private BigDecimal nextBuy;
}
