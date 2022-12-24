package a.b.c.trace.model;

import a.b.c.base.annotation.Remark;
import a.b.c.exchange.enums.OrderSide;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class StockOrder  {

    @Remark("主键")
    private Long id;

    @Remark("交易对")
    private String symbol;

    @Remark("订单id")
    private String clientOrderId;

    @Remark("价格")
    private BigDecimal price;

    @Remark("订单类型")
    private String type;

    @Remark("交易数量")
    private BigDecimal quantity;

    @Remark("时间")
    private Date createdAt;

    @Remark("原始信息")
    private String ori;

    @Remark("完成")
    private BigDecimal finish;

    private OrderSide orderSide;

}
