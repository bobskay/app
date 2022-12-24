package a.b.c.exchange.dto;

import a.b.c.base.util.DateTime;
import lombok.Data;
import a.b.c.exchange.response.ApiResponse;

import java.math.BigDecimal;

@Data
public class Kline extends ApiResponse {
    private DateTime time;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
}
