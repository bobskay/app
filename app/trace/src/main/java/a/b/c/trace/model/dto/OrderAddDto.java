package a.b.c.trace.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderAddDto {
    private BigDecimal price;
    private BigDecimal quantity;
}
