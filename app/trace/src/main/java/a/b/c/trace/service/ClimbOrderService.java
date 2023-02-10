package a.b.c.trace.service;

import a.b.c.base.util.IdWorker;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.exchange.enums.OrderState;
import a.b.c.trace.enums.ClimbState;
import a.b.c.trace.enums.Currency;
import a.b.c.trace.mapper.ClimbOrderMapper;
import a.b.c.trace.model.ClimbOrder;
import a.b.c.trace.model.TaskInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ClimbOrderService {

    @Resource
    ClimbOrderMapper ClimbOrderMapper;

    public List<ClimbOrder> running() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("running", 1);
        List<ClimbOrder> list = ClimbOrderMapper.selectList(queryWrapper);
        return list;
    }

    public void toLose(Exchange exchange,BigDecimal sellPrize, ClimbOrder order, TaskInfo taskInfo) {
        String clientId=Long.toString(taskInfo.getId(), Character.MAX_RADIX) + "_" + order.getId();
        exchange.order(OrderSide.SELL, BigDecimal.ZERO, order.getQuantity(),clientId);
        order.setState(ClimbState.lose.toString());
        order.setRunning(0);
        order.setSellPrize(sellPrize);
        ClimbOrderMapper.updateById(order);
    }

    public void toWin(Exchange exchange, BigDecimal sellPrize, ClimbOrder order, TaskInfo taskInfo) {
        String clientId=Long.toString(taskInfo.getId(), Character.MAX_RADIX) + "_" + order.getId();
        exchange.order(OrderSide.SELL, BigDecimal.ZERO, order.getQuantity(),clientId);
        order.setState(ClimbState.win.toString());
        order.setRunning(0);
        order.setSellPrize(sellPrize);
        ClimbOrderMapper.updateById(order);
    }

    public void newOrder(Exchange exchange,Currency currency,BigDecimal buyPrice, BigDecimal amount,TaskInfo taskInfo) {
        BigDecimal quantity=amount.divide(buyPrice).setScale(currency.getQuantityScale(), RoundingMode.DOWN);
        String clientId=Long.toString(taskInfo.getId(), Character.MAX_RADIX) + "_" + IdWorker.nextLong();
        ClimbOrder climbOrder=new ClimbOrder();
        climbOrder.setIdx(0);
        climbOrder.setCurrency(currency);
        climbOrder.setSymbol(currency.usdt());
        climbOrder.setState(ClimbState.unknown.toString());
        climbOrder.setBuyPrice(buyPrice);
        climbOrder.setQuantity(quantity);

        exchange.toUsdt(currency,quantity,clientId);
        ClimbOrderMapper.insert(climbOrder);
    }
}
