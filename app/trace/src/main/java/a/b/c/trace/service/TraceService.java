package a.b.c.trace.service;

import a.b.c.Constant;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.exchange.enums.OrderState;
import a.b.c.trace.component.socket.listener.AggTradeListener;
import a.b.c.trace.mapper.StockOrderMapper;
import a.b.c.trace.model.StockOrder;
import a.b.c.trace.model.vo.AccountVo;
import a.b.c.trace.model.vo.OpenOrderVo;
import a.b.c.trace.model.vo.RuleConfig;
import a.b.c.trace.model.vo.RuleInfo;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

@Service
@Slf4j
public class TraceService {

    @Resource
    private OrderService orderService;
    @Resource
    private MarketService marketService;
    @Resource
    private UserConfigService userConfigService;

    @Resource
    AggTradeListener aggTradeListener;

    @Resource
    AccountInfoService accountInfoService;

    @Resource
    StockOrderMapper stockOrderMapper;

    private ReentrantLock lock = new ReentrantLock();

    public void doTrace() {
        lock.lock();
        try {
            doTraceInner();
        } finally {
            lock.unlock();
        }
    }


    private void doTraceInner() {
        BigDecimal current = aggTradeListener.getPrice();
        if (current == null) {
            log.info("价格未初始化,等待下次交易");
            return;
        }
        AccountVo accountVo = accountInfoService.getAccount(current);
        RuleInfo info=userConfigService.getRuleInfo();
        if(accountVo.getHold().compareTo(info.getMaxHold())>0){
            log.info("已到达最大持仓,暂停交易");
            return;
        }

        RuleConfig rule = accountVo.getRuleConfig();
        log.debug("{}获取规则:买入减少{}卖出增加{},每{}秒缩减{}"
                , accountVo.getHold(), rule.getBuySub(), rule.getSellAdd(), rule.getStepSecond(), rule.getStep());
        doBuy(current, accountVo, rule);
        doSell(current, accountVo, rule);

    }

    private void doSell(BigDecimal current, AccountVo accountVo, RuleConfig rule) {
//        if (accountVo.getHold().compareTo(BigDecimal.ZERO) == 0) {
//            log.info("当前持仓为0,跳过卖出");
//            return;
//        }
//        OpenOrderVo sell = accountVo.minSell();
//        if (sell == null) {
//            BigDecimal price = current.add(rule.getSellAdd());
//            marketService.newSell(price, rule.getQuantity());
//            return;
//        }
//        BigDecimal expectPrice = sell.getExpectPrice();
//        //卖出价随时间减少不会增加
//        if (sell.getPrice().compareTo(expectPrice) < 0) {
//            return;
//        }
//        if (sell.getPrice().subtract(expectPrice).abs().compareTo(BigDecimal.ONE) > 0) {
//            log.debug("卖出价格从{}更新为{}", sell.getPrice(), expectPrice);
//            marketService.updatePrice(sell, expectPrice);
//        }
    }

    private void doBuy(BigDecimal current, AccountVo accountVo, RuleConfig rule) {
//        OpenOrderVo buy = accountVo.maxBuy();
//        if (buy == null) {
//            BigDecimal price = current.subtract(rule.getBuySub());
//            log.debug("当前没有买单,准备新增" + price + "," + rule.getQuantity());
//            marketService.newBuy(price, rule.getQuantity());
//            return;
//        }
//        BigDecimal expectPrice = buy.getExpectPrice();
//        //挂的买单只会随着时间逐渐增加,如果当前委托的买单价格大于期望价格,就跳过
//        if (buy.getPrice().compareTo(expectPrice) > 0) {
//            return;
//        }
//        if (buy.getPrice().subtract(expectPrice).abs().compareTo(BigDecimal.ONE) > 0) {
//            log.debug("买入价格从{}更新为{}", buy.getPrice(), expectPrice);
//            marketService.updatePrice(buy, expectPrice);
//        }
    }

    public void orderUpdate(JSONObject js) {
        lock.lock();
        try {
            orderConfirmInner(js);
        } catch (Exception e) {
            log.error("消费订单更新消息出错:" + e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }


    public void orderConfirmInner(JSONObject js) throws Exception {
//        log.debug("收到订单变更消息：" + JsonUtil.PRETTY_MAPPER.writeValueAsString(js));
//        JSONObject orderJs = js.getJSONObject("o");
//        StockOrder order = toStockOrder(orderJs);
//
//        if(!order.getSymbol().equalsIgnoreCase(Constant.SYMBOL)){
//            return;
//        }
//
//        if (OrderState.FILLED.toString().equalsIgnoreCase(order.getType())) {
//            if (OrderSide.BUY == order.getOrderSide()) {
//                accountInfoService.buyFinish(order);
//            }
//            if(OrderSide.SELL==order.getOrderSide()){
//                accountInfoService.sellFinish(order);
//            }
//            stockOrderMapper.insert(order);
//        }
    }

    private StockOrder toStockOrder(JSONObject orderJs) {
        String type = orderJs.getString("X");
        String orderId = orderJs.getString("c");
        String symbol = orderJs.getString("s");
        BigDecimal price = orderJs.getBigDecimal("ap");
        BigDecimal quantity = orderJs.getBigDecimal("q");
        BigDecimal finish = orderJs.getBigDecimal("z");
        String side=orderJs.getString("S");

        StockOrder stockOrder = new StockOrder();
        stockOrder.setType(type);
        stockOrder.setSymbol(symbol);
        stockOrder.setClientOrderId(orderId);
        stockOrder.setPrice(price);
        stockOrder.setQuantity(quantity);
        stockOrder.setOri(orderJs.toString());
        stockOrder.setFinish(finish);
        stockOrder.setOrderSide(OrderSide.valueOf(side));
        stockOrder.setOri(orderJs.toString());
        stockOrder.setCreatedAt(new Date());
        return stockOrder;
    }
}
