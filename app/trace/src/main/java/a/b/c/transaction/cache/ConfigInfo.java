package a.b.c.transaction.cache;

import a.b.c.trace.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Data
public class ConfigInfo {
    private Long interval= TimeUnit.MINUTES.toMillis(60);
    private BigDecimal minInterval=new BigDecimal("3");
    private BigDecimal sellAdd =new BigDecimal("10");
    private BigDecimal down=new BigDecimal("11");
    private String symbol= Currency.ETH.busd();
    private Integer scale=2;
    private BigDecimal maxHold=new BigDecimal("20");
    private BigDecimal quantity=new BigDecimal("0.01");
    private BigDecimal quantity1=new BigDecimal("0.5");
    private BigDecimal quantity2=new BigDecimal("0.4");
    private BigDecimal quantity3=new BigDecimal("0.3");
    private BigDecimal quantity4=new BigDecimal("0.2");
    private BigDecimal quantity5=new BigDecimal("0.1");
    private BigDecimal hold1=new BigDecimal("3");
    private BigDecimal hold2=new BigDecimal("5");
    private BigDecimal hold3=new BigDecimal("10");
    private BigDecimal hold4=new BigDecimal("20");
    private BigDecimal hold5=new BigDecimal("50");
}
