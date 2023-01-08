package a.b.c.trace.model.dto;

import a.b.c.trace.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SpotSellDto {
    private Long taskInfoId;
    private Currency currency;
    private BigDecimal quantity;
    private BigDecimal price;

}
