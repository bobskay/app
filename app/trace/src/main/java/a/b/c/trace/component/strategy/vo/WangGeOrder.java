package a.b.c.trace.component.strategy.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class WangGeOrder {
    private String name;
    private Date createdAt;
    private BigDecimal price;
    private BigDecimal quantity;
    private String clientOrderId;
    private String type;
}
