package a.b.c.trace.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OpenOrderUpdateDto {
    @NotNull
    private String clientOrderId;
    @NotNull
    private BigDecimal price;
}
