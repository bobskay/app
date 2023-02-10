package a.b.c.trace.component.strategy;

import a.b.c.base.util.IdWorker;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.trace.component.strategy.vo.ClimbData;
import a.b.c.trace.component.strategy.vo.ClimbStep;
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

public class ClimbTest {

    @Test
    public void run() {
        TaskInfo taskInfo = newTaskInfo();
        taskInfo.setIntervalSecond(60);
        taskInfo.setTaskState(TaskState.waiting);
        taskInfo.setCreatedAt(new Date());
        taskInfo.setUpdatedAt(new Date());
        taskInfo.setNextAt(new Date());
        taskInfo.setStrategy(Strategy.CLIMB);
        taskInfo.setRunCount(0);
        taskInfo.setErrorCount(0);
        System.out.println(JsonUtil.toJs(taskInfo));

    }

    private TaskInfo newTaskInfo() {
        ClimbData config = new ClimbData();
        config.setLose(new BigDecimal("0.9"));
        config.setAmount(new BigDecimal(100));
        config.setMax(10);
        config.setSteps(steps());
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setId(IdWorker.nextLong());
        taskInfo.setData(JsonUtil.toJs(config));
        return taskInfo;
    }

    private List<ClimbStep> steps() {
        List<ClimbStep> list = new ArrayList();
        list.add(newStep(0.05, 0.02));
        list.add(newStep(0.1, 0.05));
        list.add(newStep(0.2, 0.1));
        list.add(newStep(0.3, 0.2));
        list.add(newStep(0.4, 0.3));
        list.add(newStep(0.5, 0.4));
        list.add(newStep(0.6, 0.5));
        list.add(newStep(0.8, 0.6));
        list.add(newStep(1, 0.8));
        list.add(newStep(1.5, 1));
        list.add(newStep(2, 1.5));
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setIdx(i + 1);
        }
        return list;
    }

    private ClimbStep newStep(double add, double sub) {
        ClimbStep step = new ClimbStep();
        step.setAdd(new BigDecimal(add).setScale(2, RoundingMode.DOWN));
        step.setSub(new BigDecimal(sub).setScale(2, RoundingMode.DOWN));
        return step;
    }
}
