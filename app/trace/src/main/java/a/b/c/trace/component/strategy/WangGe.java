package a.b.c.trace.component.strategy;

import a.b.c.base.util.json.JsonUtil;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.Account;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.trace.component.strategy.info.WangGeInfo;
import a.b.c.trace.component.strategy.vo.*;
import a.b.c.trace.mapper.TraceOrderMapper;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.service.TraceOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component
@Slf4j
public class WangGe implements Strategy<WangGeData> {
    private Map<Long, WangGeInfo> infoMap=new ConcurrentHashMap<>();
    @Resource
    TraceOrderService traceOrderService;
    @Resource
    TraceOrderMapper traceOrderMapper;

    @Override
    public void run(TaskInfo taskInfo) {
        this.update(taskInfo);
    }

    @Override
    public void filled(TaskInfo taskInfo, TraceOrder db) {
        getInfoMap(taskInfo.getId()).lastFilled(db);
        WangGeData wangGeData = JsonUtil.toBean(taskInfo.getData(), WangGeData.class);
        Exchange exchange = Exchange.getInstance(wangGeData.getSymbol(), wangGeData.getCurrency().getScale());
        //如果是卖单成交,不处理
        if (OrderSide.SELL.toString().equalsIgnoreCase(db.getOrderSide().toString())) {
            return;
        }
        if(db.getRefId()!=null){
            log.error("网格订单已经处理过了:"+db);
            return;
        }
        WangGeRule rule = getRule(wangGeData);
        BigDecimal sellPrice = db.getExpectPrice().add(rule.getSellAdd());
        String remark="买单成交,创建卖单";
        TraceOrder traceOrder = traceOrderService.newOrder(wangGeData.getCurrency(), taskInfo.getId()
                , wangGeData.getSymbol(), sellPrice, BigDecimal.ZERO.subtract(db.getQuantity()),remark);
        db.setRefId(traceOrder.getId());
        traceOrderMapper.updateById(db);
        exchange.order(OrderSide.SELL, sellPrice, db.getQuantity(), traceOrder.getClientOrderId());
    }

    private WangGeInfo getInfoMap(Long id) {
        WangGeInfo info=infoMap.get(id);
        if(info==null){
            info=new WangGeInfo(id);
            infoMap.put(id,info);
        }
        return info;
    }

    @Override
    public WangGeData updateData(TaskInfo taskInfo) {
        WangGeData wangGeData = JsonUtil.toBean(taskInfo.getData(), WangGeData.class);
        Exchange exchange = Exchange.getInstance(wangGeData.getSymbol(), wangGeData.getCurrency().getScale());

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

        Exchange exchange = Exchange.getInstance(wangGeData.getSymbol(), wangGeData.getCurrency().getScale());
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
        BigDecimal exceptBuy = wangGeData.getPrice().subtract(rule.getBuySub());
        if (sell != null) {
            exceptBuy = sell.getPrice().subtract(rule.getSellAdd()).subtract(rule.getBuySub());
        }

        //如果当前委托的买单比期望的低,说明价格涨了,将买单改价格提高
        if (buy != null) {
            //如果期望价格减去当前价格小于2,就直接返回
            if (exceptBuy.subtract(buy.getPrice()).compareTo(new BigDecimal(2)) <0) {
                log.debug("当前挂单正常,等待成交:"+buy.getPrice()+">"+exceptBuy+"-2");
                return;
            }
            //虽然价格过低,但如果价格离当前价很近也等待
            if(wangGeData.getPrice().subtract(buy.getPrice()).compareTo(new BigDecimal(2))<0){
                log.info("距离成交价很近了,不处理:"+wangGeData.getPrice()+"-"+buy.getPrice()+"<2");
                return;
            }
            log.info("买单价格过低,向上更新{}->{}", buy.getPrice(), exceptBuy);
            exchange.cancel(buy.getClientOrderId());
            buy = null;
        }

        //如果新的买入价>当前价格-2,买入价=当前价格-2
        BigDecimal max=wangGeData.getPrice().subtract(new BigDecimal(2)); //允许的最高买价
        if(exceptBuy.compareTo(max)>0){
            log.info("新买单价格过高{}->{}",exceptBuy,max);
            exceptBuy=max;
        }

        //无买单就挂一个买单
        if (buy == null) {
            WangGeInfo wangGeInfo=getInfoMap(taskInfo.getId());
            if(!wangGeInfo.isBuyable()){
                return;
            }
            Long busId = taskInfo.getId();
            String remark="网格创建买单";
            TraceOrder tr = traceOrderService.newOrder(wangGeData.getCurrency()
                    , busId, wangGeData.getSymbol(), exceptBuy, rule.getQuantity(),remark);
            exchange.order(OrderSide.BUY, exceptBuy, rule.getQuantity(), tr.getClientOrderId());
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
