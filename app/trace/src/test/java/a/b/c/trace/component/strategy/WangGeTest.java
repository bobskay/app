package a.b.c.trace.component.strategy;

import a.b.c.base.util.IdWorker;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.trace.component.strategy.vo.WangGeRule;
import a.b.c.trace.component.strategy.vo.WangGeData;
import a.b.c.trace.enums.Currency;
import a.b.c.trace.model.TaskInfo;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;


public class WangGeTest {

    @Test
    public void test() {
//        WangGe wangGe = new WangGe();
//        wangGe.traceOrderService = new TraceOrderServiceMock();
//
//        TaskInfo taskInfo = newWangGe();
//        wangGe.run(taskInfo);
    }

    private TaskInfo newWangGe() {
        WangGeData wangGeData = new WangGeData();
        wangGeData.setCurrency(Currency.ETH);
        wangGeData.setSymbol(Currency.ETH.usdt());
        wangGeData.setRules(new ArrayList<>());
        addRule(wangGeData, null, 1,5, 0.1);
        addRule(wangGeData, 1, 2,5, 0.1);
        addRule(wangGeData, 2, 3,5, 0.1);

        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setId(IdWorker.nextLong());
        taskInfo.setData(JsonUtil.toJs(wangGeData));
        return taskInfo;
    }

    private void addRule(WangGeData wangGeData, Integer min, Integer max, Integer step,double quantity) {
        WangGeRule r = new WangGeRule();
        if (min != null) {
            r.setMin(new BigDecimal(min));
        }
        if (max != null) {
            r.setMax(new BigDecimal(max));
        }
        r.setBuySub(new BigDecimal(step));
        r.setSellAdd(new BigDecimal(step));
        r.setQuantity(new BigDecimal(quantity).setScale(wangGeData.getCurrency().getQuantityScale(), RoundingMode.DOWN));
        wangGeData.getRules().add(r);
    }

}