package a.b.c.exchange.dto;

import lombok.Data;
import a.b.c.exchange.response.ApiResponse;

import java.math.BigDecimal;

@Data
public class Price extends ApiResponse {
    private String symbol;
    private BigDecimal price;
    private Long time;
}
