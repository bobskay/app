package a.b.c.trace.model;

import a.b.c.base.annotation.Remark;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.exchange.enums.OrderState;
import a.b.c.trace.enums.TraceOrderType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 交易任务对应的订单
 */
@Data
public class TraceOrder {

    private Long id;
    @Remark("对应业务id")
    private Long businessId;
    @Remark("下单价")
    private BigDecimal expectPrice;
    @Remark("实际交易价")
    private BigDecimal price;
    private TraceOrderType traceOrderType;
    private String clientOrderId;
    private String symbol;
    private BigDecimal quantity;
    private OrderSide orderSide;
    private Date createdAt;
    private Date finishAt;
    private Date updatedAt;
    private OrderState orderState;

}
