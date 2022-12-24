package a.b.c.trace.service;

import a.b.c.exchange.dto.OpenOrder;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.trace.mapper.OrderInfoMapper;
import a.b.c.trace.model.OrderInfo;
import a.b.c.exchange.Exchange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;

@Service
@Slf4j
public class MarketService {


    @Resource
    private OrderInfoMapper orderInfoMapper;


    @Resource
    AccountInfoService accountInfoService;

    Exchange exchange;


    public void updatePrice(OpenOrder openOrder, BigDecimal exceptPrice,String clientOrderId) {
        exchange.cancel(openOrder.getClientOrderId());
        OrderSide side = OrderSide.valueOf(openOrder.getSide());
        exchange.order(side, exceptPrice, openOrder.getOrigQty(), clientOrderId);

    }

    public String newSell(BigDecimal price, BigDecimal quantity,String clientOrderId) {
        log.info("新增委托:" + clientOrderId + "  " + price + "  " + quantity);
        exchange.order(OrderSide.SELL, price, quantity, clientOrderId);
        return clientOrderId;
    }

    public void finish(OrderInfo orderInfo, BigDecimal confirmPrice) {

    }

    public String newBuy(BigDecimal price, BigDecimal quantity,String clientOrderId) {
        log.info("新增委托:" + clientOrderId + "  " + price + "  " + quantity);
        exchange.order(OrderSide.BUY, price, quantity, clientOrderId);
        return clientOrderId;
    }

    public void cancel(String clientOrderId) {
        exchange.cancel(clientOrderId);
    }
}
