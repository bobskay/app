package a.b.c.trace.component.strategy;

import a.b.c.base.util.CollectionUtil;
import a.b.c.exchange.Exchange;
import a.b.c.trace.component.strategy.vo.ClimbData;
import a.b.c.trace.component.strategy.vo.ClimbStep;
import a.b.c.trace.enums.ClimbState;
import a.b.c.trace.enums.Currency;
import a.b.c.trace.model.ClimbOrder;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.service.ClimbOrderService;
import a.b.c.trace.service.TraceOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class Climb implements Strategy<ClimbData> {

    @Resource
    private ClimbOrderService climbOrderService;
    @Resource
    TraceOrderService traceOrderService;

    @Override
    public void run(TaskInfo taskInfo) {
        Exchange exchange = Exchange.getInstance(null, Currency.USDT.getScale());
        ClimbData data = updateData(taskInfo);
        List<String> symbols = CollectionUtil.getField(data.getOrderList(), d -> d.getSymbol());
        Map<String, BigDecimal> prices = exchange.getPrice(symbols);
        List<ClimbOrder> runningOrders = climbOrderService.running();
        boolean createNew = true;
        Set<Currency> exist = new HashSet<>();
        int running = runningOrders.size();
        for (ClimbOrder order : runningOrders) {
            exist.add(order.getCurrency());
            BigDecimal current = prices.get(order.getSymbol());
            ClimbState state = ClimbState.valueOf(order.getState());
            BigDecimal lose = order.getBuyPrice().multiply(data.getLose());
            if (current.compareTo(lose) <= 0) {
                running--;
                climbOrderService.toLose(exchange, current, order, taskInfo);
                continue;
            }
            if (state == ClimbState.unknown) {
                List<ClimbStep> steps = data.getSteps();
                Collections.sort(steps, (o1, o2) -> o2.getAdd().compareTo(o1.getAdd()));
                for (ClimbStep step : steps) {
                    BigDecimal prize = current.add(current.multiply(step.getAdd()));
                    if (current.compareTo(prize) > 0) {
                        order.setIdx(step.getIdx());
                        break;
                    }
                }
                if (order.getIdx() == 0) {
                    createNew = false;
                }
                continue;
            }
            if (state == ClimbState.save) {
                ClimbStep step = data.getSteps().stream()
                        .filter(d -> d.getIdx().equals(order.getIdx()))
                        .collect(Collectors.toList()).get(0);
                BigDecimal min = current.add(current.multiply(step.getSub()));
                if (current.compareTo(min) < 0) {
                    running--;
                    climbOrderService.toWin(exchange, current, order, taskInfo);
                    break;
                }
            }

            if (running >= data.getMax()) {
                log.info("到达上限：" + running);
                return;
            }

            if (!createNew) {
                log.info("有计算中订单");
                return;
            }

            ClimbOrder od = newOrder(exist);
            if (od == null) {
                log.info("无匹配货币");
                return;
            }
            climbOrderService.newOrder(exchange, od.getCurrency(), od.getBuyPrice(), data.getAmount(), taskInfo);
        }
    }

    /**
     * @param exist 已经持有的
     */
    private ClimbOrder newOrder(Set<Currency> exist) {
       //TODO
        return null;
    }


    @Override
    public void filled(TaskInfo taskInfo, TraceOrder db) {

    }

    @Override
    public ClimbData updateData(TaskInfo taskInfo) {
        return new ClimbData();
    }
}
