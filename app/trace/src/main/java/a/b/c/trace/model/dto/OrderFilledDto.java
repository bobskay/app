package a.b.c.trace.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderFilledDto {
    private Long taskInfoId;
    private BigDecimal price;
    private String orderSide;
    private BigDecimal quantity;
}
