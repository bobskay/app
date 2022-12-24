package a.b.c.trace.component.strategy;

import a.b.c.base.util.IdWorker;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.Account;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.trace.component.strategy.vo.*;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.service.TraceOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;


@Component
@Slf4j
public class WangGe implements Strategy {

    @Resource
    TraceOrderService traceOrderService;

    @Override
    public void run(TaskInfo taskInfo) {
        this.update(taskInfo, true);
    }

    public void update(TaskInfo taskInfo, boolean send) {
        WangGeData wangGeData = JsonUtil.toBean(taskInfo.getData(), WangGeData.class);
        Exchange exchange = Exchange.getInstance(wangGeData.getSymbol(), wangGeData.getCurrency().scale());

        BigDecimal cPrice = exchange.getPrice(wangGeData.getSymbol());
        BigDecimal hold = hold(wangGeData, exchange);

        wangGeData.setHold(hold);
        wangGeData.setPrice(cPrice);
        log.info("当前持仓{},价格{}", hold, cPrice);
        WangGeOrderCreator creator = new WangGeOrderCreator(send, taskInfo, wangGeData, exchange, traceOrderService);
        WangGeOrders orders = creator.doTrace();
        wangGeData.setWangGeOrders(orders);
        taskInfo.setData(JsonUtil.toJs(wangGeData));
    }

    @Override
    public TaskInfo sync(TaskInfo taskInfo) {
        update(taskInfo, false);
        return taskInfo;
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
        return hold;
    }
}
