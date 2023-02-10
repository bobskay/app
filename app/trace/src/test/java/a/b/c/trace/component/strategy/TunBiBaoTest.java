package a.b.c.trace.component.strategy;

import a.b.c.base.util.IdWorker;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.trace.component.strategy.vo.CurrencyHold;
import a.b.c.trace.component.strategy.vo.TunBiBaoData;
import a.b.c.trace.enums.Currency;
import a.b.c.trace.enums.TaskState;
import a.b.c.trace.model.TaskInfo;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TunBiBaoTest{

    @Test
    public void run() {
//        MarketConfig.test=true;
//        TaskInfo taskInfo=newTaskInfo();
//        TunBiBao tunBiBao=new TunBiBao();
//        tunBiBao.run(taskInfo);
        TaskInfo taskInfo=newTaskInfo();
        taskInfo.setIntervalSecond(60);
        taskInfo.setTaskState(TaskState.waiting);
        taskInfo.setCreatedAt(new Date());
        taskInfo.setUpdatedAt(new Date());
        taskInfo.setNextAt(new Date());
        taskInfo.setStrategy(Strategy.TUN_BI_BAO);
        taskInfo.setRunCount(0);
        taskInfo.setErrorCount(0);
        System.out.println(JsonUtil.toJs(taskInfo));

    }

    private TaskInfo newTaskInfo() {
        TunBiBaoData config=new TunBiBaoData();
        List<CurrencyHold> holdList=new ArrayList<>();
        holdList.add(newCurrencyHold(Currency.BNB,0.2));
        holdList.add(newCurrencyHold(Currency.DOT,0.2));
        holdList.add(newCurrencyHold(Currency.FIL,0.2));
        holdList.add(newCurrencyHold(Currency.ONE,0.2));
        holdList.add(newCurrencyHold(Currency.GALA,0.2));
        config.setCurrency(holdList);
        config.setRebalance(new BigDecimal(0.01));
        config.setInitUsdt(new BigDecimal(1000));
        config.setCurrentUsdt(config.getInitUsdt());

        TaskInfo taskInfo=new TaskInfo();
        taskInfo.setId(IdWorker.nextLong());
        taskInfo.setData(JsonUtil.toJs(config));
        return taskInfo;
    }

    private CurrencyHold newCurrencyHold(Currency currency,double percent) {
        CurrencyHold hold=new CurrencyHold();
        hold.setCurrency(currency);
        hold.setHold(new BigDecimal(percent).multiply(new BigDecimal(100)));
        hold.setHold(hold.getHold().setScale(hold.getCurrency().getQuantityScale(), RoundingMode.DOWN));
        hold.setPercent(new BigDecimal(percent));
        hold.setPrice(new BigDecimal(0));
        return hold;
    }
}