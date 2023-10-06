package a.b.c.trace.service;

import a.b.c.Constant;
import a.b.c.base.util.DateTime;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.Account;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.trace.cache.ConfigCache;
import a.b.c.trace.enums.TraceState;
import a.b.c.trace.model.TraceInfo;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.model.dto.TraceReportDto;
import a.b.c.trace.model.vo.TraceOrderVo;
import a.b.c.trace.model.vo.TraceReportVo;
import a.b.c.trace.model.vo.WangGeVo;
import a.b.c.transaction.cache.ConfigInfo;
import a.b.c.transaction.cache.RunInfo;
import a.b.c.transaction.utils.OrderIdUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class WangGeService {

    @Resource
    TraceInfoService traceInfoService;
    @Resource
    ConfigService configService;

    @Getter
    private RunInfo runInfo = new RunInfo();

    public void doTrace() {
        ConfigInfo configInfo = configService.wangGeConfig();
        Exchange exchange = getExchange();

        BigDecimal hold = hold(exchange);
        if (hold.compareTo(configInfo.getMaxHold()) > 0) {
            log.info("已经到达持仓上限：" + hold);
            return;
        }

        String symbol = configInfo.getSymbol();
        BigDecimal price = exchange.getPrice(symbol);
        price=price.setScale(configInfo.getScale());

        if (runInfo.getHighPrice() == null || runInfo.getHighPrice().compareTo(price) < 0) {
            runInfo.setHighPrice(price);
        }

        Date buyTime = runInfo.getBuyTime();
        if (buyTime != null) {
            if (System.currentTimeMillis() - buyTime.getTime() < configInfo.getInterval()) {
                long diff= configInfo.getInterval()-(System.currentTimeMillis() - buyTime.getTime());
                log.info("刚买过,还需等待:{}",DateTime.showHourTime(diff));
                return;
            }
        }

        BigDecimal quantity = quantity(hold, configInfo);
        BigDecimal lastSell = lastSell(exchange, configInfo, price);

        if (runInfo.getHighPrice().subtract(price).compareTo(configInfo.getDown()) < 0) {
            log.info("距离最高点还不够{}-{}={}<{}", runInfo.getHighPrice(), price, runInfo.getHighPrice().subtract(price), configInfo.getSellAdd());
            return;
        }

        BigDecimal expect = lastSell.subtract(configInfo.getMinInterval()).subtract(configInfo.getSellAdd());
        if (price.compareTo(expect) > 0) {
            log.info("距离最近卖出太近：{}>{},最近卖价{}", price, expect, lastSell);
            return;
        }

        doBuy(price,quantity,exchange,configInfo);
    }

    public Exchange getExchange() {
        ConfigInfo configInfo = configService.getByKey(ConfigCache.WANG_GE_CONFIG, new ConfigInfo());
        String symbol = configInfo.getSymbol();
        int scale = configInfo.getScale();
        Exchange exchange = Exchange.getInstance(symbol, scale);
        return exchange;
    }

    public void filled(TraceOrder traceOrder) {
        if (traceOrder.getOrderSide() == OrderSide.BUY) {
            buSuccess(traceOrder);
        } else {
            sellSuccess(traceOrder);
        }
    }

    private void buSuccess(TraceOrder traceOrder) {
        ConfigInfo configInfo = configService.getByKey(ConfigCache.WANG_GE_CONFIG, new ConfigInfo());

        TraceInfo traceInfo = traceInfoService.getByBuyId(traceOrder.getClientOrderId());
        if (traceInfo == null) {
            traceInfo = new TraceInfo();
            traceInfo.setBuyStart(new Date());
            traceInfo.setQuantity(traceOrder.getQuantity());
            BigDecimal price=traceOrder.getPrice().setScale(configInfo.getScale());
            traceInfo.setBuyId(OrderIdUtil.buy(price, traceOrder.getQuantity()));
            traceInfoService.insert(traceInfo);
        }

        BigDecimal sellPrice = traceOrder.getPrice().add(configInfo.getSellAdd());
        sellPrice=sellPrice.setScale(configInfo.getScale());
        String id = OrderIdUtil.sell(sellPrice, traceInfo.getQuantity());

        traceInfo.setBuyPrice(traceOrder.getPrice());
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
        return getExchange().openOrders();
    }

    private BigDecimal hold(Exchange exchange) {
        if(!Constant.DO_TRACE){
            return new BigDecimal(0);
        }
        for(Account.PositionsDTO positionsDTO:exchange.account(exchange.getSymbol()).getPositions()){
            if(exchange.getSymbol().equalsIgnoreCase(positionsDTO.getSymbol())){
                return positionsDTO.getPositionAmt();
            }
        }
        return new BigDecimal(0);
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
        Exchange exchange = getExchange();
        ConfigInfo configInfo = configService.wangGeConfig();

        String buyInterval = "00:00";
        if (runInfo.getBuyTime() != null) {
            long duration = System.currentTimeMillis() - runInfo.getBuyTime().getTime();
            if (duration < configInfo.getInterval()) {
                buyInterval = DateTime.showHourTime(configInfo.getInterval()-duration);
            }
        }

        BigDecimal current = exchange.getPrice(configInfo.getSymbol());
        BigDecimal high = runInfo.getHighPrice();
        if (high == null || high.compareTo(current)<0) {
            high = current;
            runInfo.setHighPrice(high);
        }

        BigDecimal lastSell = lastSell(exchange, configInfo, current);
        BigDecimal lastBuy = lastSell.subtract(configInfo.getSellAdd());
        BigDecimal nextBuy = high.subtract(configInfo.getDown());
        if (nextBuy.subtract(lastBuy).abs().compareTo(configInfo.getMinInterval()) < 0) {
            nextBuy = lastBuy.subtract(configInfo.getMinInterval());
        }

        BigDecimal hold=hold(exchange);
        WangGeVo vo = new WangGeVo();
        vo.setBuyInterval(buyInterval);
        vo.setCurrent(current);
        vo.setLastSell(lastSell);
        vo.setLastBuy(lastBuy);
        vo.setNextBuy(nextBuy);
        vo.setHigh(high);
        vo.setHold(hold);
        vo.setQuantity(quantity(hold,configInfo));
        return vo;
    }

    private BigDecimal lastSell(Exchange exchange, ConfigInfo configInfo, BigDecimal current) {
        BigDecimal lastSell = null;
        List<OpenOrder> openOrders = exchange.openOrders(configInfo.getSymbol());
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
        BigDecimal hold=hold(exchange);
        BigDecimal quantity=quantity(hold,configInfo);
        BigDecimal prize=exchange.getPrice(exchange.getSymbol());
        doBuy(prize,quantity,exchange,configInfo);
    }

    public void doBuy(BigDecimal price,BigDecimal quantity,Exchange exchange,ConfigInfo configInfo) {
        price=price.setScale(configInfo.getScale());
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
            traceOrder.setPrice(Constant.MOCK_PRICE);
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
        traceOrder.setPrice(Constant.MOCK_PRICE);
        this.filled(traceOrder);
    }


}
