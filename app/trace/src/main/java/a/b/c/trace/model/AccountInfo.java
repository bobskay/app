package a.b.c.trace.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountInfo {
    private Long id;
    private BigDecimal hold;
    private BigDecimal avgPrice;
    private String currency;
    private Date lastSell;
    private Date lastBuy;
    private String buyPrices;
    /**
     * 最近成交价
     * */
    private BigDecimal confirmPrice;

}
