package a.b.c.trace.component.strategy;


import a.b.c.base.util.IdWorker;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.trace.component.strategy.vo.WangGeData;
import a.b.c.trace.component.strategy.vo.WangGeOrder;
import a.b.c.trace.component.strategy.vo.WangGeOrders;
import a.b.c.trace.component.strategy.vo.WangGeRule;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.service.TraceOrderService;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class WangGeOrderCreator {
    private List<OpenOrder> openOrders;
    private WangGeRule rule;
    private TraceOrderService traceOrderService;
    private WangGeData wangGeData;
    private TaskInfo taskInfo;
    private Exchange exchange;
    private BigDecimal currentPrice;
    private boolean send;

    /**
     * @param send 是否向远程发送交易信息
     * */
    public WangGeOrderCreator(boolean send,TaskInfo taskInfo, WangGeData wangGeData, Exchange exchange, TraceOrderService traceOrderService) {
        this.send=send;
        this.taskInfo = taskInfo;
        this.wangGeData = wangGeData;
        this.traceOrderService = traceOrderService;
        this.rule = getRule();
        this.currentPrice = wangGeData.getPrice();
        List<OpenOrder> openOrders = exchange.openOrders(wangGeData.getSymbol());
        //过滤不是当前任务开头的订单
        openOrders = openOrders.stream().filter(o -> o.getClientOrderId()
                .startsWith(IdWorker.toString(taskInfo.getId()))).collect(Collectors.toList());
        //订单价格从高到低排序
        Collections.sort(openOrders, (o1, o2) -> o2.getPrice().compareTo(o1.getPrice()));
        this.openOrders = openOrders;
        this.exchange = exchange;
        //如果持仓大于最大持仓send也等于false
        if(wangGeData.getMaxHold().compareTo(wangGeData.getHold().abs())<=0){
            log.warn("已经到达最大持仓,暂停交易");
            this.send=false;
        }
    }

    WangGeOrders doTrace() {
        if (openOrders.size() == 0) {
            return init();
        }
        if (openOrders.size() == 1) {
            return trace1(openOrders.get(0));
        }
        if (openOrders.size() == 2) {
            return trace2(openOrders.get(0), openOrders.get(1));
        }
        if (openOrders.size() == 3) {
            return trace3(openOrders.get(0), openOrders.get(1), openOrders.get(2));
        }

        if (openOrders.size() == 4) {
            WangGeOrders orders = new WangGeOrders();
            orders.setSell2(toWangGeOrder(openOrders.get(0)));
            orders.setSell1(toWangGeOrder(openOrders.get(1)));
            orders.setBuy1(toWangGeOrder(openOrders.get(2)));
            orders.setBuy2(toWangGeOrder(openOrders.get(3)));
            return orders;
        }
        throw new RuntimeException("未实现");
    }

    /**
     * 有2个委托单
     * 1. 都高于当前价,如果卖1大于区间就都取消重新初始化
     * 2. 都低于当前价,如果买1低于区间就都取消重新初始化
     * 3. 任意1个在区间内,新增2个2
     */
    private WangGeOrders trace2(OpenOrder high, OpenOrder low) {
        if (low.getPrice().compareTo(currentPrice) > 0) {
            //卖1区间外全取消
            BigDecimal dff = low.getPrice().subtract(currentPrice);
            if (dff.compareTo(rule.getStep()) > 0) {
                exchange.cancel(high.getClientOrderId());
                exchange.cancel(low.getClientOrderId());
                return init();
            } else {
                //卖1区间内,新增2个买
                WangGeOrders orders = new WangGeOrders();
                orders.setSell1(toWangGeOrder(low));
                orders.setSell2(toWangGeOrder(high));

                BigDecimal buy1 = low.getPrice().subtract(rule.getStep()).subtract(rule.getStep());
                orders.setBuy1(newBuy(buy1));
                BigDecimal buy2 = buy1.subtract(rule.getStep());
                orders.setBuy1(newBuy(buy2));
                return orders;
            }
        }
        if (high.getPrice().compareTo(currentPrice) < 0) {
            BigDecimal dff = currentPrice.subtract(high.getPrice());
            //买1区间外全取消
            if (dff.compareTo(rule.getStep()) > 0) {
                if(send){
                    exchange.cancel(high.getClientOrderId());
                    exchange.cancel(low.getClientOrderId());
                }
                return init();
            } else {
                //买1区间内,新增2个卖
                WangGeOrders orders = new WangGeOrders();
                orders.setBuy1(toWangGeOrder(high));
                orders.setBuy2(toWangGeOrder(low));

                BigDecimal sell1 = high.getPrice().add(rule.getStep()).add(rule.getStep());
                orders.setSell1(newSell(sell1));
                BigDecimal sell2 = sell1.add(rule.getStep());
                orders.setSell2(newSell(sell2));
                return orders;
            }
        }
        //任意1个在区间内,新增2个2
        WangGeOrders orders = new WangGeOrders();
        orders.setBuy1(toWangGeOrder(low));
        orders.setSell1(toWangGeOrder(high));
        BigDecimal sell2 = orders.getSell1().getPrice().add(rule.getStep());
        orders.setSell2(newSell(sell2));

        BigDecimal buy2 = orders.getBuy1().getPrice().subtract(rule.getStep());
        orders.setBuy2(newBuy(buy2));
        return orders;
    }

    /**
     * 有3个委托单
     * 1 中间价格高于当前价当作卖1
     * 2 中间价格低于当前价当作买1
     */
    private WangGeOrders trace3(OpenOrder high, OpenOrder middle, OpenOrder low) {
        WangGeOrders orders = new WangGeOrders();
        if (middle.getPrice().compareTo(currentPrice) > 0) {
            orders.setBuy1(toWangGeOrder(low));
            BigDecimal buy2 = orders.getBuy1().getPrice().subtract(rule.getStep());
            orders.setBuy2(newBuy(buy2));
            //如果最low和当前价格差距在step内说明buy1成交了,降低sell价格
            if(currentPrice.subtract(low.getPrice()).compareTo(rule.getStep())<0){
                exchange.cancel(high.getClientOrderId());
                orders.setSell2(toWangGeOrder(middle));
                BigDecimal sell1=orders.getSell2().getPrice().subtract(rule.getStep());
                orders.setSell1(newSell(sell1));
            }else{
                orders.setSell1(toWangGeOrder(middle));
                orders.setSell2(toWangGeOrder(high));
            }
        } else {
            orders.setSell1(toWangGeOrder(high));
            BigDecimal sell2 = orders.getSell1().getPrice().add(rule.getStep());
            orders.setSell2(newSell(sell2));
            //如果hig和价格差距在区间内,说明sell1交易成功,提高buy的价格
            if(high.getPrice().subtract(currentPrice).compareTo(rule.getStep())<0){
                exchange.cancel(low.getClientOrderId());
                orders.setBuy2(toWangGeOrder(middle));
                BigDecimal buy1=orders.getBuy2().getPrice().add(rule.getStep());
                orders.setBuy2(newBuy(buy1));
            }else{
                orders.setBuy1(toWangGeOrder(middle));
                orders.setBuy2(toWangGeOrder(low));
            }
        }
        return orders;
    }

    //只有1个订单,如果比当前价格高,就当作卖1处理,否则当作买1
    private WangGeOrders trace1(OpenOrder openOrder) {
        WangGeOrders orders = new WangGeOrders();
        if (openOrder.getPrice().compareTo(currentPrice) > 0) {
            orders.setSell1(toWangGeOrder(openOrder));
            orders.setSell2(newSell(openOrder.getPrice().add(rule.getStep())));
            //买1价格=卖1-step*2
            BigDecimal buy1 = openOrder.getPrice().subtract(rule.getStep().multiply(new BigDecimal(2)));
            orders.setBuy1(newBuy(buy1));
            BigDecimal buy2 = orders.getBuy1().getPrice().subtract(rule.getStep());
            orders.setBuy2(newBuy(buy2));
        } else {
            orders.setBuy1(toWangGeOrder(openOrder));
            orders.setBuy2(newBuy(openOrder.getPrice().subtract(rule.getStep())));
            //卖1价格=买1+step*2
            BigDecimal sell1 = openOrder.getPrice().add(rule.getStep().multiply(new BigDecimal(2)));
            orders.setSell1(newSell(sell1));
            BigDecimal sell2 = orders.getSell1().getPrice().add(rule.getStep());
            orders.setSell2(newSell(sell2));
        }
        return orders;
    }

    private WangGeOrder newSell(BigDecimal price) {
        if (price.compareTo(wangGeData.getPrice()) < 0) {
            throw new RuntimeException("无法委托卖出价比当前价格还低:" + price + "<" + wangGeData.getPrice());
        }
        TraceOrder traceOrder = traceOrderService.newOrder(wangGeData.getCurrency(), taskInfo.getId(),
                wangGeData.getSymbol(), price, BigDecimal.ZERO.subtract(rule.getQuantity()));
        if(send){
            exchange.order(OrderSide.SELL,price,rule.getQuantity(),traceOrder.getClientOrderId());
        }
        return toWangGeOrder(traceOrder);
    }

    private WangGeOrder newBuy(BigDecimal price) {
        if (price.compareTo(wangGeData.getPrice()) > 0) {
            throw new RuntimeException("无法委托买入价比当前价格还高:" + price + ">" + wangGeData.getPrice());
        }
        TraceOrder traceOrder = traceOrderService.newOrder(wangGeData.getCurrency(), taskInfo.getId(),
                wangGeData.getSymbol(), price, rule.getQuantity());
        if(send){
            exchange.order(OrderSide.BUY,price,rule.getQuantity(),traceOrder.getClientOrderId());
        }
        return toWangGeOrder(traceOrder);
    }

    private WangGeOrder toWangGeOrder(TraceOrder traceOrder) {
        WangGeOrder order = new WangGeOrder();
        order.setPrice(traceOrder.getExpectPrice());
        order.setQuantity(traceOrder.getQuantity());
        order.setCreatedAt(traceOrder.getCreatedAt());
        order.setClientOrderId(traceOrder.getClientOrderId());
        order.setType("traceOrder");
        return order;
    }

    private WangGeOrder toWangGeOrder(OpenOrder openOrder) {
        WangGeOrder order = new WangGeOrder();
        order.setPrice(openOrder.getPrice());
        order.setQuantity(openOrder.getOrigQty());
        order.setCreatedAt(openOrder.getTime());
        order.setClientOrderId(openOrder.getClientOrderId());
        order.setType("openOrder");
        return order;
    }

    //无委托中的订单直接根据当前价格上下各挂2个
    private WangGeOrders init() {
        WangGeOrders orders = new WangGeOrders();
        BigDecimal price = currentPrice.add(rule.getStep());
        WangGeOrder sell1 = newSell(price);
        orders.setSell1(sell1);

        price = price.add(rule.getStep());
        WangGeOrder sell2 = (newSell(price));
        orders.setSell2(sell2);

        price = currentPrice.subtract(rule.getStep());
        WangGeOrder buy1 = (newBuy(price));
        orders.setBuy1(buy1);

        price = price.subtract(rule.getStep());
        WangGeOrder buy2 = (newBuy(price));
        orders.setBuy2(buy2);

        if(send){
            exchange.order(OrderSide.SELL, sell2.getPrice(), sell2.getQuantity(), sell2.getClientOrderId());
            exchange.order(OrderSide.SELL, sell1.getPrice(), sell2.getQuantity(), sell1.getClientOrderId());
            exchange.order(OrderSide.BUY, buy1.getPrice(), sell2.getQuantity(), buy1.getClientOrderId());
            exchange.order(OrderSide.BUY, buy2.getPrice(), sell2.getQuantity(), buy2.getClientOrderId());
        }
        return orders;
    }

    private WangGeRule getRule() {
        BigDecimal hold = wangGeData.getHold();
        for (WangGeRule rule : wangGeData.getRules()) {
            //当前持仓大于等于最小
            if (rule.getMin() == null || hold.compareTo(rule.getMin()) >= 0) {
                //当前持仓大于小于最大
                if (rule.getMax() == null || hold.compareTo(rule.getMax()) < 0) {
                    return rule;
                }
            }
        }
        log.info("当前持仓规则:" + wangGeData.getRules());
        throw new RuntimeException("没有和当前持仓量:" + hold);
    }
}
