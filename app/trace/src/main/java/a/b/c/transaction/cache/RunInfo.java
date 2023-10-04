package a.b.c.transaction.cache;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class RunInfo {
    private BigDecimal highPrice;
    private Date buyTime;

}
