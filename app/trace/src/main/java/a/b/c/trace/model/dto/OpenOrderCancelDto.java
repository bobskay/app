package a.b.c.trace.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class OpenOrderCancelDto {
    @NotNull
    private String clientOrderId;
    private String symbol;
}
