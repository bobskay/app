package a.b.c.exchange.vo;

import a.b.c.base.util.DateTime;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BalanceVo {
    private BigDecimal total;
    private BigDecimal available;
    private DateTime updated;
    private String symbol;
}
