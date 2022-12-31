package a.b.c.trace.component.socket.listener;

import a.b.c.base.util.json.EnumUtil;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.exchange.enums.OrderState;
import a.b.c.exchange.socket.listener.AccountListener;
import a.b.c.trace.component.strategy.Strategy;
import a.b.c.trace.enums.TraceOrderType;
import a.b.c.trace.mapper.TraceOrderMapper;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.service.TaskInfoService;
import a.b.c.trace.service.TraceOrderService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Slf4j
@Component
public class OrderTradeUpdateListener implements AccountListener {

    @Resource
    TraceOrderService traceOrderService;
    @Resource
    TraceOrderMapper traceOrderMapper;
    @Resource
    TaskInfoService taskInfoService;

    @Override
    public boolean listen(JSONObject js) {
        try {
            JSONObject orderJs = js.getJSONObject("o");
            String clientOrderId = orderJs.getString("c");
            String orderState = orderJs.getString("X");
            TraceOrder db = traceOrderService.getByClientOrderId(clientOrderId);
            if (db == null) {
                log.warn("订单不存在:"+clientOrderId);
                newOrder(orderJs);
                return true;
            }
            //状态没变直接返回
            if(db.getOrderState().equalsIgnoreCase(orderState)){
                return true;
            }
            db.setOrderState(orderState);
            traceOrderService.updateState(db);


            if(db.getOrderState().equalsIgnoreCase(OrderState.FILLED.toString())){
                if(db.getTraceOrderType()==TraceOrderType.task){
                    TaskInfo taskInfo=taskInfoService.getById(db.getBusinessId());
                    //如果是网格订单完成了,就执行网格的订单完成方法
                    if(Strategy.WANG_GE.equalsIgnoreCase(taskInfo.getStrategy())){
                        taskInfoService.filled(taskInfo,db);
                    }
                }
            }
        } catch (Exception ex) {
            log.error("更新订单出错：" + ex.getMessage(), ex);
        }
        return true;
    }

    private void newOrder(JSONObject orderJs) {
        TraceOrder traceOrder=new TraceOrder();
        String orderState = orderJs.getString("X");
        String clientOrderId = orderJs.getString("c");
        BigDecimal price = orderJs.getBigDecimal("ap");
        BigDecimal quantity = orderJs.getBigDecimal("q");
        String symbol=orderJs.getString("s");
        String orderSide=orderJs.getString("S");

        traceOrder.setOrderState(orderState);
        traceOrder.setSymbol(symbol);
        traceOrder.setClientOrderId(clientOrderId);
        traceOrder.setExpectPrice(price);
        traceOrder.setPrice(price);
        traceOrder.setQuantity(quantity);
        traceOrder.setCreatedAt(new Date());
        traceOrder.setOrderSide(EnumUtil.get(OrderSide.class,orderSide));
        if(OrderState.FILLED.toString().equalsIgnoreCase(orderState)){
            Long time=orderJs.getLong("T");
            traceOrder.setFinishAt(new Date(time));
            traceOrder.setUpdatedAt(new Date());
        }
        traceOrder.setBusinessId(0L);
        traceOrder.setTraceOrderType(TraceOrderType.error);
        traceOrderMapper.insert(traceOrder);
    }

    public String eventName() {
        return "ORDER_TRADE_UPDATE";
    }

}
