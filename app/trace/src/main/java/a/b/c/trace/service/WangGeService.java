package a.b.c.trace.service;

import a.b.c.Constant;
import a.b.c.base.util.DateTime;
import a.b.c.base.util.json.DateUtil;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.Account;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.trace.cache.ConfigCache;
import a.b.c.trace.cache.HoldCache;
import a.b.c.trace.cache.OpenOrderCache;
import a.b.c.trace.component.socket.listener.AggTradeListener;
import a.b.c.trace.enums.TraceState;
import a.b.c.trace.model.TraceInfo;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.model.vo.WangGeVo;
import a.b.c.transaction.cache.ConfigInfo;
import a.b.c.transaction.cache.RunInfo;
import a.b.c.transaction.utils.OrderIdUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class WangGeService {

    @Resource
    TraceInfoService traceInfoService;
    @Resource
    ConfigService configService;
    @Resource
    OpenOrderCache openOrderCache;
    @Resource
    AggTradeListener aggTradeListener;
    @Resource
    HoldCache holdCache;

    @Getter
    private RunInfo runInfo = new RunInfo();

    public void doTrace() {
        ConfigInfo configInfo = configService.wangGeConfig();
        Exchange exchange = getExchange();

        BigDecimal hold = holdCache.get(configInfo.getSymbol());
        if (hold.compareTo(configInfo.getMaxHold()) > 0) {
            log.info("已经到达持仓上限：" + hold);
            return;
        }

        BigDecimal price = aggTradeListener.getPrice();
        price=price.setScale(configInfo.getScale(),RoundingMode.DOWN);

        if (runInfo.getHighPrice() == null || runInfo.getHighPrice().compareTo(price) < 0) {
            runInfo.setHighPrice(price);
        }

        Date buyTime = runInfo.getBuyTime();
        BigDecimal down = getDown(configInfo, buyTime);

        BigDecimal quantity = quantity(hold, configInfo);
        BigDecimal lastSell = lastSell(price);

        if (runInfo.getHighPrice().subtract(price).compareTo(down) < 0) {
            log.info("距离最高点还不够{}-{}={}<{}", runInfo.getHighPrice(), price, runInfo.getHighPrice().subtract(price), down);
            return;
        }

        BigDecimal expect = lastSell.subtract(configInfo.getMinDown()).subtract(configInfo.getSellAdd());
        if (price.compareTo(expect) > 0) {
            log.info("距离最近卖出太近：{}>{},最近卖价{}", price, expect, lastSell);
            return;
        }
        doBuy(quantity,exchange,configInfo);
    }

    private static BigDecimal getDown(ConfigInfo configInfo, Date buyTime) {
        if (buyTime != null) {
            long diff=(System.currentTimeMillis() - buyTime.getTime())/1000/60;
            if(diff< configInfo.getTime1()){
                return configInfo.getDown1();
            }
            if(diff< configInfo.getTime2()){
                return configInfo.getDown2();
            }
            if(diff< configInfo.getTime3()){
                return configInfo.getDown3();
            }
            if(diff< configInfo.getTime4()){
                return configInfo.getDown4();
            }
            if(diff< configInfo.getTime5()){
                return configInfo.getDown5();
            }
        }
        return configInfo.getDown5();
    }

    public Exchange getExchange() {
        ConfigInfo configInfo = configService.getByKey(ConfigCache.WANG_GE_CONFIG, new ConfigInfo());
        String symbol = configInfo.getSymbol();
        int scale = configInfo.getScale();
        Exchange exchange = Exchange.getInstance(symbol, scale);
        return exchange;
    }

    public void filled(TraceOrder traceOrder) {
        if(traceOrder.getSymbol()!=null){
            removeCache(traceOrder.getSymbol());
        }
        if (traceOrder.getOrderSide() == OrderSide.BUY) {
            buSuccess(traceOrder);
        } else {
            sellSuccess(traceOrder);
        }
    }

    private void removeCache(String symbol) {
        openOrderCache.invalidate(symbol);
        holdCache.invalidate(symbol);
    }

    private void buSuccess(TraceOrder traceOrder) {
        ConfigInfo configInfo = configService.getByKey(ConfigCache.WANG_GE_CONFIG, new ConfigInfo());

        TraceInfo traceInfo = traceInfoService.getByBuyId(traceOrder.getClientOrderId());
        if (traceInfo == null) {
            traceInfo = new TraceInfo();
            traceInfo.setBuyStart(new Date());
            traceInfo.setQuantity(traceOrder.getQuantity());
            BigDecimal price=traceOrder.getPrice().setScale(configInfo.getScale(),RoundingMode.DOWN);
            traceInfo.setBuyId(OrderIdUtil.buy(price, traceOrder.getQuantity()));
            traceInfoService.insert(traceInfo);
        }

        BigDecimal sellPrice = traceOrder.getPrice().add(configInfo.getSellAdd());
        sellPrice=sellPrice.setScale(configInfo.getScale(), RoundingMode.DOWN);
        String id = OrderIdUtil.sell(sellPrice, traceInfo.getQuantity());

        traceInfo.setBuyPrice(traceOrder.getPrice().setScale(configInfo.getScale(), RoundingMode.DOWN));
        traceInfo.setSellStart(new Date());
        traceInfo.setSellId(id);
        traceInfo.setTraceState(TraceState.selling);
        traceInfoService.updateById(traceInfo);

        Exchange exchange = getExchange();
        exchange.order(OrderSide.SELL, sellPrice, traceInfo.getQuantity(), id);
    }

    private void sellSuccess(TraceOrder traceOrder) {
        TraceInfo traceInfo = traceInfoService.getBuSellId(traceOrder.getClientOrderId());
        if (traceInfo == null) {
            log.info("未知的卖出订单：" + traceOrder.getClientOrderId());
            return;
        }
        traceInfo.setSellPrice(traceOrder.getPrice());
        traceInfo.setSellEnd(new Date());
        BigDecimal profit = traceInfo.getSellPrice().subtract(traceInfo.getBuyPrice()).multiply(traceInfo.getQuantity());
        traceInfo.setProfit(profit);
        Long seconds=traceInfo.getSellEnd().getTime()-traceInfo.getSellStart().getTime();
        traceInfo.setDurationSeconds(seconds/1000L);
        traceInfo.setTraceState(TraceState.finished);
        traceInfoService.updateById(traceInfo);

        runInfo.setBuyTime(null);
        runInfo.setHighPrice(traceOrder.getPrice());
    }

    public List<OpenOrder> openOrders() {
        return openOrderCache.get(getExchange().getSymbol());
    }



    private static BigDecimal quantity(BigDecimal hold, ConfigInfo configInfo) {
        if (hold.compareTo(configInfo.getHold1()) <= 0) {
            return configInfo.getQuantity1();
        }
        if (hold.compareTo(configInfo.getHold2()) <= 0) {
            return configInfo.getQuantity2();
        }
        if (hold.compareTo(configInfo.getHold3()) <= 0) {
            return configInfo.getQuantity3();
        }
        if (hold.compareTo(configInfo.getHold4()) <= 0) {
            return configInfo.getQuantity4();
        }
        if (hold.compareTo(configInfo.getHold5()) <= 0) {
            return configInfo.getQuantity5();
        }
        log.error("找不到{}对应的购买数量", hold);
        return configInfo.getQuantity();

    }

    public WangGeVo wangGeInfo() {
        ConfigInfo configInfo = configService.wangGeConfig();

        BigDecimal current = aggTradeListener.getPrice();
        BigDecimal high = runInfo.getHighPrice();
        if (high == null || high.compareTo(current)<0) {
            high = current;
            runInfo.setHighPrice(high);
        }

        BigDecimal lastSell = lastSell(current);
        BigDecimal lastBuy = lastSell.subtract(configInfo.getSellAdd());
        BigDecimal down=getDown(configInfo,runInfo.getBuyTime());
        BigDecimal nextBuy = high.subtract(down);
        BigDecimal minNext=lastSell.subtract(configInfo.getMinDown()).subtract(configInfo.getSellAdd());
        if (nextBuy.compareTo(minNext)>0) {
            nextBuy = minNext;
        }

        String buyDiff="0";
        if(runInfo.getBuyTime()!=null){
            long diff=System.currentTimeMillis()-runInfo.getBuyTime().getTime();
            buyDiff= DateTime.showHourTime(diff);
        }

        BigDecimal hold= holdCache.get(configInfo.getSymbol());
        WangGeVo vo = new WangGeVo();
        vo.setCurrent(current);
        vo.setLastSell(lastSell);
        vo.setLastBuy(lastBuy);
        vo.setHigh(high);
        vo.setHold(hold);
        vo.setMinNext(minNext);
        vo.setNextBuy(nextBuy);
        vo.setQuantity(quantity(hold,configInfo));
        vo.setBuyDiff(buyDiff);
        return vo;
    }

    private BigDecimal lastSell(BigDecimal current) {
        BigDecimal lastSell = null;
        List<OpenOrder> openOrders =this.openOrders();
        if(openOrders==null){
            openOrders=new ArrayList<>();
        }
        for (OpenOrder openOrder : openOrders) {
            if (openOrder.getSide().equalsIgnoreCase(OrderSide.SELL.toString())) {
                if (lastSell == null || openOrder.getPrice().compareTo(lastSell) < 0) {
                    lastSell = openOrder.getPrice();
                }
            }
        }
        if (lastSell == null) {
            return runInfo.getHighPrice() == null ? current : runInfo.getHighPrice();
        }
        return lastSell;
    }

    public void doBuy(){
        ConfigInfo configInfo=configService.wangGeConfig();
        Exchange exchange=getExchange();
        BigDecimal hold= holdCache.get(configInfo.getSymbol());
        BigDecimal quantity=quantity(hold,configInfo);
        doBuy(quantity,exchange,configInfo);
    }

    /**
     * 市价买入
     * */
    public void doBuy(BigDecimal quantity,Exchange exchange,ConfigInfo configInfo) {
        BigDecimal price=aggTradeListener.getPrice().setScale(configInfo.getScale(),RoundingMode.DOWN);
        String id = OrderIdUtil.buy(price, quantity);
        TraceInfo traceInfo = new TraceInfo();
        traceInfo.setBuyId(id);
        traceInfo.setBuyStart(new Date());
        traceInfo.setTraceState(TraceState.buying);
        traceInfo.setQuantity(quantity);
        traceInfoService.insert(traceInfo);
        runInfo.setBuyTime(new Date());
        runInfo.setHighPrice(price);
        exchange.order(OrderSide.BUY, BigDecimal.ZERO, quantity, id);
    }

    public void doFilled(String clientOrderId) {
        TraceInfo traceInfo=traceInfoService.getByBuyId(clientOrderId);
        if(traceInfo!=null){
            log.info("模拟买单成交："+clientOrderId);
            TraceOrder traceOrder=new TraceOrder();
            traceOrder.setOrderSide(OrderSide.BUY);
            traceOrder.setClientOrderId(clientOrderId);
            traceOrder.setPrice(aggTradeListener.getPrice());
            this.filled(traceOrder);
            return;
        }

        traceInfo=traceInfoService.getBuSellId(clientOrderId);
        if(traceInfo==null){
            throw new RuntimeException("订单不存在");
        }
        log.info("模拟卖单成交："+clientOrderId);
        TraceOrder traceOrder=new TraceOrder();
        traceOrder.setOrderSide(OrderSide.SELL);
        traceOrder.setClientOrderId(clientOrderId);
        traceOrder.setPrice(aggTradeListener.getPrice());
        this.filled(traceOrder);
    }


}
