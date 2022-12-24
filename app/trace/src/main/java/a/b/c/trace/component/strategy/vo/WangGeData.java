package a.b.c.trace.component.strategy.vo;

import a.b.c.trace.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WangGeData {
    private BigDecimal maxHold;
    private List<WangGeRule> rules;
    private String symbol;
    private Currency currency;
    private BigDecimal hold;
    private BigDecimal price;
    private WangGeOrders wangGeOrders;
}

