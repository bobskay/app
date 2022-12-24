package a.b.c.trace.component.strategy;

import a.b.c.MarketConfig;
import a.b.c.base.util.IdWorker;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.exchange.Exchange;
import a.b.c.trace.component.strategy.vo.CurrencyHold;
import a.b.c.trace.component.strategy.vo.TunBiBaoData;
import a.b.c.trace.enums.Currency;
import a.b.c.trace.model.TaskInfo;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class TunBiBaoTest{

    @Test
    public void run() {
        MarketConfig.test=true;
        TaskInfo taskInfo=newTaskInfo();
        TunBiBao tunBiBao=new TunBiBao();
        tunBiBao.run(taskInfo);


    }

    private TaskInfo newTaskInfo() {
        TunBiBaoData config=new TunBiBaoData();
        List<CurrencyHold> holdList=new ArrayList<>();
        holdList.add(newCurrencyHold(Currency.ETH,0.33));
        holdList.add(newCurrencyHold(Currency.PEOPLE,0.33));
        holdList.add(newCurrencyHold(Currency.BTC,0.33));
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
        hold.setPercent(new BigDecimal(percent));
        return hold;
    }
}