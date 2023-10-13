package a.b.c.transaction.cache;

import a.b.c.Constant;
import a.b.c.trace.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Data
public class ConfigInfo {

    private BigDecimal sellAdd =new BigDecimal("10");
    private String symbol= Constant.SYMBOL;
    private Integer scale=2;
    //最小间隔
    private BigDecimal minDown=new BigDecimal("2");

    //持续购买间隔，
    private Integer time1=1;
    private Integer time2=5;
    private Integer time3=10;
    private Integer time4=30;
    private Integer time5=60;
    private BigDecimal down1=new BigDecimal(50);
    private BigDecimal down2=new BigDecimal(30);
    private BigDecimal down3=new BigDecimal(20);
    private BigDecimal down4=new BigDecimal(10);
    private BigDecimal down5=new BigDecimal(5);


    //持仓多少时买入量
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
