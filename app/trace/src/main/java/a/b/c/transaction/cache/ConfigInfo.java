package a.b.c.transaction.cache;

import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Data
public class ConfigInfo {
    private Long interval= TimeUnit.MINUTES.toMillis(60);
    private BigDecimal minInterval=new BigDecimal("5");
    private BigDecimal sellAdd =new BigDecimal("10");
    private BigDecimal quantity=new BigDecimal("0.1");
    private String symbol="eth/busd";
    private Integer scale=2;
    private BigDecimal maxHold=new BigDecimal("20");
}
