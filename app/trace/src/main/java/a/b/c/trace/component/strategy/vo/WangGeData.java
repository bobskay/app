package a.b.c.trace.component.strategy.vo;

import a.b.c.exchange.dto.OpenOrder;
import a.b.c.trace.enums.Currency;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class WangGeData implements StrategyData{
    private BigDecimal maxHold;
    private List<WangGeRule> rules;
    private String symbol;
    private Currency currency;
    private BigDecimal hold;
    private BigDecimal price;
    //当前卖出最高价
    private BigDecimal maxSell;
    private WangGeRule rule;
    @JsonIgnore
    private List<OpenOrder> openOrders;
}

