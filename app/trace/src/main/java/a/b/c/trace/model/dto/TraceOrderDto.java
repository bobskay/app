package a.b.c.trace.model.dto;

import a.b.c.base.dto.PageQueryDto;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.exchange.enums.OrderState;
import a.b.c.trace.model.TraceOrder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class TraceOrderDto extends PageQueryDto<TraceOrder> {
    private Date createdAtStart;
    private Date createdAtEnd;
    private Date updatedAtStart;
    private Date updatedAtEnd;
    private String symbol;
    private OrderSide orderSide;
    private List<String> orderStateList;
    private String clientOrderId;
}
