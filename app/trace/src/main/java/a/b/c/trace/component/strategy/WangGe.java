package a.b.c.trace.component.strategy;

import a.b.c.base.util.IdWorker;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.Account;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.exchange.dto.OpenOrders;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.trace.component.strategy.vo.*;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.service.TraceOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;


@Component
@Slf4j
public class WangGe implements Strategy {

    @Resource
    TraceOrderService traceOrderService;

    @Override
    public void run(TaskInfo taskInfo) {
        this.update(taskInfo);
    }

    @Override
    public void filled(TaskInfo taskInfo, TraceOrder db) {
        WangGeData wangGeData = JsonUtil.toBean(taskInfo.getData(), WangGeData.class);
        Exchange exchange = Exchange.getInstance(wangGeData.getSymbol(), wangGeData.getCurrency().scale());
        //如果是卖单成交,不处理
        if (OrderSide.SELL.toString().equalsIgnoreCase(db.getOrderSide().toString())) {
            return;
        }
        WangGeRule rule = getRule(wangGeData);
        BigDecimal sellPrice = db.getExpectPrice().add(rule.getSellAdd());
        TraceOrder traceOrder = traceOrderService.newOrder(wangGeData.getCurrency(), db.getId()
                , wangGeData.getSymbol(), sellPrice, db.getQuantity());
        exchange.order(OrderSide.SELL, sellPrice, rule.getQuantity(), traceOrder.getClientOrderId());
    }

    @Override
    public WangGeData updateData(TaskInfo taskInfo) {
        WangGeData wangGeData = JsonUtil.toBean(taskInfo.getData(), WangGeData.class);
        Exchange exchange = Exchange.getInstance(wangGeData.getSymbol(), wangGeData.getCurrency().scale());
        List<OpenOrder> openOrders = exchange.openOrders(wangGeData.getSymbol());
        //委托单按照倒叙排列
        Collections.sort(openOrders, (o1, o2) -> o2.getPrice().compareTo(o1.getPrice()));
        BigDecimal currentPrice = exchange.getPrice(wangGeData.getSymbol());
        BigDecimal hold = hold(wangGeData, exchange);

        wangGeData.setHold(hold);
        wangGeData.setOpenOrders(openOrders);
        wangGeData.setPrice(currentPrice);
        wangGeData.setRule(getRule(wangGeData));

        if(openOrders.size()>0){
            wangGeData.setMaxSell(openOrders.get(0).getPrice());
        }else{
            wangGeData.setMaxSell(wangGeData.getPrice());
        }

        return wangGeData;
    }

    public void update(TaskInfo taskInfo) {
        WangGeData wangGeData = updateData(taskInfo);
        if (wangGeData.getHold().compareTo(wangGeData.getMaxHold()) >= 0) {
            log.debug("当前持仓>=最大持仓{}>={}", wangGeData.getHold(), wangGeData.getMaxHold());
            return;
        }
        Exchange exchange = Exchange.getInstance(wangGeData.getSymbol(), wangGeData.getCurrency().scale());
        OpenOrder buy = null;//价格最高的买单
        OpenOrder sell = null;//价格最低的卖单
        for (OpenOrder o : wangGeData.getOpenOrders()) {
            if (OrderSide.BUY.toString().equalsIgnoreCase(o.getSide())) {
                buy = o;
                break;
            }
            if (OrderSide.SELL.toString().equalsIgnoreCase(o.getSide())) {
                sell = o;
            }
        }
        WangGeRule rule = getRule(wangGeData);

        log.debug("委托订单数量:{}", wangGeData.getOpenOrders().size());
        if(buy!=null){
            log.debug("buy:"+buy.getPrice()+"");
        }
        if(sell!=null){
            log.debug("sell:"+sell.getPrice()+",预期买价:{}",
                    sell.getPrice().subtract(rule.getSellAdd()).subtract(rule.getBuySub()));
        }
        BigDecimal buyPrice = wangGeData.getPrice().subtract(rule.getBuySub());
        if (sell != null) {
            buyPrice = sell.getPrice().subtract(rule.getSellAdd()).subtract(rule.getBuySub());
        }
        //如果当前委托的买单比期望的低,说明价格涨了,将买单改价格提高
        if (buy != null) {
            //价差超过2重新挂单
            if (buy.getPrice().subtract(buyPrice).compareTo(new BigDecimal(2)) <0) {
                log.debug("当前挂单正常,等待成交:"+buy.getPrice()+">"+buyPrice+"-2");
                return;
            }
            log.info("买单价格过低,向上更新{}->{}", buy.getPrice(), buyPrice);
            exchange.cancel(buy.getClientOrderId());
            buy = null;
        }

        //无买单就挂一个买单
        if (buy == null) {
            //如果新的买入价>当前价格-2,买入价=当前价格-2
            BigDecimal min=wangGeData.getPrice().subtract(new BigDecimal(2));
            if(buyPrice.compareTo(min)>0){
                log.info("新买单价格过低{}->{}",buyPrice,min);
                buyPrice=min;
            }
            Long busId = taskInfo.getId();
            TraceOrder tr = traceOrderService.newOrder(wangGeData.getCurrency()
                    , busId, wangGeData.getSymbol(), buyPrice, rule.getQuantity());
            exchange.order(OrderSide.BUY, buyPrice, rule.getQuantity(), tr.getClientOrderId());
        }
        //openOrders太多,不持久化
        wangGeData.setOpenOrders(null);
        taskInfo.setData(JsonUtil.toJs(wangGeData));
    }

    private BigDecimal hold(WangGeData wangGeData, Exchange exchange) {
        BigDecimal hold = BigDecimal.ZERO;
        Account account = exchange.account(wangGeData.getSymbol());
        for (Account.PositionsDTO positionsDTO : account.getPositions()) {
            if (positionsDTO.getSymbol().equalsIgnoreCase(exchange.getSymbol())) {
                //持有
                hold = positionsDTO.getPositionAmt();
                break;
            }
        }
        wangGeData.setHold(hold);
        return hold;
    }

    private WangGeRule getRule(WangGeData wangGeData) {
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
