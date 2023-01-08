package a.b.c.trace.component.strategy;

import a.b.c.base.annotation.Remark;
import a.b.c.base.util.CollectionUtil;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.response.Order;
import a.b.c.trace.component.strategy.vo.CurrencyHold;
import a.b.c.trace.component.strategy.vo.TunBiBaoData;
import a.b.c.trace.enums.Currency;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.service.TraceOrderService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Component
@Slf4j

public class TunBiBao implements Strategy {
    public static final BigDecimal MIN=new BigDecimal(10);


    @Resource
    TraceOrderService traceOrderService;

    @Override
    public void run(TaskInfo dbTask) {
        Exchange exchange = Exchange.getInstance(null, Currency.USDT.scale());
        TunBiBaoData data =updateData(dbTask);

        BigDecimal total = data.getCurrentUsdt();
        BigDecimal diffAmount = data.getDiff();
        BigDecimal maxDiff = total.multiply(data.getRebalance().setScale(Currency.USDT.scale(), RoundingMode.DOWN));
        log.debug("最大差额:{},配置差额={}",
                diffAmount.setScale(2, RoundingMode.DOWN),
                maxDiff.setScale(2, RoundingMode.DOWN));
        if (diffAmount.compareTo(maxDiff) < 0) {
            log.debug("未达到重平价格,直接返回");
            return;
        }

        //为防止余额不足,先卖后买
        Map<CurrencyHold, BigDecimal> sells = new LinkedHashMap<>();
        Map<CurrencyHold, BigDecimal> buys = new LinkedHashMap<>();

        for (CurrencyHold hold : data.getCurrency()) {
            BigDecimal expect = total.multiply(hold.getPercent());
            BigDecimal diff = expect.subtract(hold.getHold().multiply(hold.getPrice()));
            //要购买的金额小于5U,跳过
            if (diff.abs().compareTo(MIN) < 0) {
                log.info("订单金额小于"+MIN+",跳过:"+hold.getCurrency()+":"+diff);
                continue;
            }
            BigDecimal quantity = diff.divide(hold.getPrice(), hold.getCurrency().quantityScale(), RoundingMode.DOWN);
            if (quantity.compareTo(BigDecimal.ZERO) > 0) {
                buys.put(hold, quantity);
            } else {
                sells.put(hold, quantity);
            }
            log.debug("{}变更后持仓:{}+{}={},变动价格{}", hold.getCurrency(), hold.getHold(),
                    quantity, hold.getHold().add(quantity), diff);
            hold.setHold(hold.getHold().add(quantity));
            hold.setUsdt(hold.getHold().multiply(hold.getPrice()));
        }

        sells.putAll(buys);
        Long businessId = dbTask.getId();
        sells.forEach((hold, quantity) -> {
            String remark="屯币宝正常下单";
            TraceOrder traceOrder = traceOrderService
                    .newOrder(hold.getCurrency(), businessId, hold.getCurrency().usdt(),
                            hold.getPrice(), quantity,remark);
            Order order = exchange.toUsdt(hold.getCurrency(), quantity, traceOrder.getClientOrderId());
            if (order == null) {
                throw new RuntimeException("下单失败");
            }
        });
        data.setCurrentUsdt(total);
        dbTask.setData(JsonUtil.toJs(data));
        log.info("更新后的任务:" + JsonUtil.prettyJs(data));
    }

    @Override
    public void filled(TaskInfo taskInfo, TraceOrder db) {
        log.info("订单成交:" + db.getClientOrderId());
    }

    public TunBiBaoData updateData(TaskInfo taskInfo) {
        Exchange exchange = Exchange.getInstance(null, Currency.USDT.scale());
        TunBiBaoData data = JsonUtil.toBean(taskInfo.getData(), TunBiBaoData.class);
        log.debug("初始数据:" + JsonUtil.toJs(data));
        List<String> symbols = CollectionUtil.getField(data.getCurrency(), d -> d.getCurrency().usdt());
        Map<String, BigDecimal> prices = exchange.getPrice(symbols);
        Map<String, BigDecimal> assets = exchange.assetMap();

        BigDecimal total = new BigDecimal(0);
        for (CurrencyHold currencyHold : data.getCurrency()) {
            BigDecimal price = prices.get(currencyHold.getCurrency().usdt());
            BigDecimal hold = assets.get(currencyHold.getCurrency().toString());
            if (hold == null) {
                log.info("找不到持仓:" + assets + ":" + currencyHold.getCurrency().toString());
                hold = BigDecimal.ZERO;
            }
            currencyHold.setHold(hold);
            BigDecimal usdt = currencyHold.getHold().multiply(price);
            currencyHold.setUsdt(usdt);
            currencyHold.setPrice(price);
            log.debug("{}持仓:{}*{}={}", currencyHold.getCurrency(), currencyHold.getHold(),
                    currencyHold.getPrice(), currencyHold.getHold().multiply(currencyHold.getPrice()));
            total = total.add(currencyHold.getUsdt());
        }
        log.debug("总价:" + total);
        //如果总额为0说明未初始化
        if (total.compareTo(BigDecimal.ZERO) == 0) {
            total = data.getInitUsdt();
        }

        data.setCurrentUsdt(total);
        BigDecimal diffAmount = data.getDiff();
        BigDecimal maxDiff = total.multiply(data.getRebalance().setScale(Currency.USDT.scale(), RoundingMode.DOWN));
        log.debug("最大差额:{},配置差额={}",
                diffAmount.setScale(2, RoundingMode.DOWN),
                maxDiff.setScale(2, RoundingMode.DOWN));
        data.setCurrentUsdt(total);
        taskInfo.setData(JsonUtil.toJs(data));
        return data;
    }


}
