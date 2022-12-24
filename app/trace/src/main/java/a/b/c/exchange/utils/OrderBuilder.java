package a.b.c.exchange.utils;




import a.b.c.exchange.dto.OrderDto;
import a.b.c.exchange.enums.*;

import java.math.BigDecimal;

public class OrderBuilder {
    public static int scale=3;


    public static OrderDto order(OrderSide orderSide, BigDecimal price, BigDecimal quantity){
        OrderDto orderDto=new OrderDto();
        orderDto.setSide(orderSide);
        orderDto.setType(OrderType.LIMIT);
        orderDto.setWorkingType(WorkingType.MARK_PRICE);
        orderDto.setNewOrderRespType(NewOrderRespType.ACK);
        orderDto.setPrice(price);
        orderDto.setQuantity(quantity);
        orderDto.setTimeInForce(TimeInForce.GTC);

        return orderDto;
    }

    public static UrlParamsBuilder builder(OrderDto orderDto){
       return UrlParamsBuilder.build()
                .putToUrl("symbol", orderDto.getSymbol())
                .putToUrl("side", orderDto.getSide())
                .putToUrl("positionSide", orderDto.getPositionSide())
                .putToUrl("type", orderDto.getType())
                .putToUrl("timeInForce", orderDto.getTimeInForce())
                .putToUrl("quantity", orderDto.getQuantity())
                .putToUrl("price", orderDto.getPrice())
                .putToUrl("reduceOnly", orderDto.getReduceOnly()+"")
                .putToUrl("newClientOrderId", orderDto.getNewClientOrderId())
                .putToUrl("stopPrice", orderDto.getStopPrice())
                .putToUrl("workingType", orderDto.getWorkingType())
                .putToUrl("newOrderRespType", orderDto.getNewOrderRespType());
    }

    public static UrlParamsBuilder spotOrder(OrderDto orderDto){
        return UrlParamsBuilder.build()
                .putToUrl("symbol", orderDto.getSymbol())
                .putToUrl("side", orderDto.getSide())
                .putToUrl("type", orderDto.getType())
                .putToUrl("quantity", orderDto.getQuantity())
                .putToUrl("newClientOrderId", orderDto.getNewClientOrderId())
                .putToUrl("newOrderRespType", orderDto.getNewOrderRespType());

    }
}
