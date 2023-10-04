package a.b.c.trace.service;

import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.trace.enums.TraceState;
import a.b.c.trace.model.TraceInfo;
import a.b.c.trace.model.TraceOrder;
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

    @Getter
    private RunInfo runInfo = new RunInfo();
    @Getter
    private ConfigInfo configInfo = new ConfigInfo();


    public void doTrace() {
        String symbol = configInfo.getSymbol();
        Exchange exchange = getExchange();
        BigDecimal price = exchange.getPrice(symbol);

        if (runInfo.getHighPrice() == null || runInfo.getHighPrice().compareTo(price) < 0) {
            runInfo.setHighPrice(price);
        }

        Date buyTime = runInfo.getBuyTime();
        if (buyTime != null) {
            if (System.currentTimeMillis() - buyTime.getTime() < configInfo.getInterval()) {
                log.info("距离上次买入间隔：{}<{}", System.currentTimeMillis() - buyTime.getTime(), configInfo.getInterval());
                return;
            }
        }

        BigDecimal lastSell = null;
        List<OpenOrder> openOrders = exchange.openOrders(symbol);
        for (OpenOrder openOrder : openOrders) {

        }
        if (lastSell == null) {
            lastSell = price.add(configInfo.getSellAdd());
        }

        BigDecimal expect = lastSell.subtract(configInfo.getMinInterval()).subtract(configInfo.getSellAdd());
        if (price.compareTo(expect) > 0) {
            log.info("距离最小买入太近：{}>{}", price, expect);
            return;
        }

        String id = OrderIdUtil.buy(price, configInfo.getQuantity());

        TraceInfo traceInfo = new TraceInfo();
        traceInfo.setBuyId(id);
        traceInfo.setBuyStart(new Date());
        traceInfo.setTraceState(TraceState.buying);
        traceInfoService.insert(traceInfo);

        runInfo.setBuyTime(new Date());

        exchange.order(OrderSide.BUY, BigDecimal.ZERO, configInfo.getQuantity(), id);
    }

    private Exchange getExchange() {
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
        TraceInfo traceInfo = traceInfoService.getByBuyId(traceOrder.getClientOrderId());
        if (traceInfo == null) {
            traceInfo = new TraceInfo();
            traceInfo.setBuyStart(new Date());
            traceInfo.setQuantity(traceOrder.getQuantity());
            traceInfo.setBuyId(OrderIdUtil.buy(traceOrder.getPrice(), traceOrder.getQuantity()));
            traceInfoService.insert(traceInfo);
        }

        BigDecimal sellPrice = traceOrder.getPrice().add(configInfo.getSellAdd());
        String id = OrderIdUtil.sell(sellPrice, configInfo.getQuantity());

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
        BigDecimal profit=traceInfo.getSellPrice().subtract(traceInfo.getBuyPrice()).multiply(traceInfo.getQuantity());
        traceInfo.setProfit(profit);
        traceInfo.setTraceState(TraceState.finished);
        traceInfoService.updateById(traceInfo);

        runInfo.setBuyTime(new Date());
    }

    public List<OpenOrder> openOrders() {
        return getExchange().openOrders();
    }
}
