package a.b.c.trace.model;

import a.b.c.trace.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ClimbOrder {
    private Long id;
    private Currency currency;
    private String symbol;
    private BigDecimal buyPrice;
    private BigDecimal sellPrize;
    private Integer running;
    private String state;
    private BigDecimal quantity;
    private Integer idx;
}
