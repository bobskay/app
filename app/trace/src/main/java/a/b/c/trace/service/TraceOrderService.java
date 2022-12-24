package a.b.c.trace.service;

import a.b.c.base.util.IdWorker;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.exchange.enums.OrderState;
import a.b.c.trace.enums.Currency;
import a.b.c.trace.enums.TraceOrderType;
import a.b.c.trace.mapper.TraceOrderMapper;
import a.b.c.trace.model.TraceOrder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Service
@Slf4j
public class TraceOrderService {


    @Resource
    TraceOrderMapper traceOrderMapper;

    public TraceOrder newOrder(Currency currency,Long businessId, String symbol, BigDecimal price, BigDecimal quantity) {
        price=price.setScale(currency.scale(), RoundingMode.DOWN);
        quantity=quantity.setScale(currency.quantityScale(),RoundingMode.DOWN);

        TraceOrder traceOrder=new TraceOrder();
        traceOrder.setId(IdWorker.nextLong());
        if (quantity.compareTo(BigDecimal.ZERO) > 0) {
            traceOrder.setOrderSide(OrderSide.BUY);
        } else {
            traceOrder.setOrderSide(OrderSide.SELL);
        }
        traceOrder.setBusinessId(businessId);
        traceOrder.setOrderState(OrderState.NEW);
        traceOrder.setQuantity(quantity);
        traceOrder.setSymbol(symbol);
        traceOrder.setTraceOrderType(TraceOrderType.task);
        traceOrder.setExpectPrice(price);
        traceOrder.setCreatedAt(new Date());
        traceOrder.setUpdatedAt(traceOrder.getCreatedAt());
        traceOrder.setId(IdWorker.nextLong());
        String clientId =Long.toString(businessId, Character.MAX_RADIX)+Long.toString(traceOrder.getId(), Character.MAX_RADIX);
        traceOrder.setClientOrderId(clientId);
        traceOrderMapper.insert(traceOrder);
        log.debug("新增订单{}:{},数量{}",symbol,price,quantity);
        return traceOrder;
    }
}
