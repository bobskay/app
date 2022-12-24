package a.b.c.trace.service;

import a.b.c.base.util.DateTime;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.base.util.log.LogUtil;
import a.b.c.trace.component.strategy.Strategy;
import a.b.c.trace.component.strategy.vo.CurrencyHold;
import a.b.c.trace.component.strategy.vo.TunBiBaoData;
import a.b.c.trace.component.strategy.vo.WangGeData;
import a.b.c.trace.component.strategy.vo.WangGeRule;
import a.b.c.trace.enums.Currency;
import a.b.c.trace.enums.TaskState;
import a.b.c.trace.mapper.TaskInfoMapper;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.vo.TaskInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class TaskInfoService {


    @Resource
    TaskInfoMapper taskInfoMapper;
    @Resource
    ApplicationContext applicationContext;

    public List<TaskInfoVo> getAll() {
        List<TaskInfo> list = taskInfoMapper.selectList(null);
        List<TaskInfoVo> ret = new ArrayList<>();
        for (TaskInfo info : list) {
            TaskInfoVo vo = toVo(info);
            ret.add(vo);
        }
        return ret;
    }

    private TaskInfoVo toVo(TaskInfo info) {
        TaskInfoVo vo = new TaskInfoVo();
        BeanUtils.copyProperties(info, vo);
        return vo;
    }

    public int insert(TaskInfo taskInfo) {
        return taskInfoMapper.insert(taskInfo);
    }

    public List<TaskInfo> waitingList() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.le("next_at", new Date());
        queryWrapper.ne("task_state", TaskState.stop);
        queryWrapper.ne("task_state", TaskState.running);
        return taskInfoMapper.selectList(queryWrapper);
    }

    public void update(TaskInfo taskInfo) {
        taskInfoMapper.updateById(taskInfo);
    }

    public TaskInfo addWangGe() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("strategy", Strategy.WANG_GE);
        TaskInfo taskInfo = taskInfoMapper.selectOne(queryWrapper);
        if (taskInfo == null) {
            log.info("新增网格任务");
            taskInfo = new TaskInfo();
            taskInfo.setTaskState(TaskState.waiting);
            taskInfo.setCreatedAt(new Date());
            taskInfo.setUpdatedAt(taskInfo.getCreatedAt());
            taskInfo.setNextAt(taskInfo.getCreatedAt());
            taskInfo.setRunCount(0);
            taskInfo.setErrorCount(0);
            taskInfo.setIntervalSecond(60);
            taskInfo.setMaxError(10);
            taskInfo.setStrategy(Strategy.WANG_GE);

            WangGeData wangGeData = new WangGeData();
            wangGeData.setCurrency(Currency.ETH);
            wangGeData.setSymbol(Currency.ETH.usdt());
            wangGeData.setRules(new ArrayList<>());
            addRule(wangGeData, null, 0, 5, 0.1);
            addRule(wangGeData, 0, 1, 5, 0.1);
            addRule(wangGeData, 1, 2, 5, 0.1);
            addRule(wangGeData, 2, 3, 5, 0.1);
            addRule(wangGeData, 3, null, 5, 0.1);
            taskInfo.setData(JsonUtil.toJs(wangGeData));
            this.insert(taskInfo);
        }
        return taskInfo;
    }

    public TaskInfo addTunBiBao() {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("strategy", Strategy.TUN_BI_BAO);
        TaskInfo taskInfo = taskInfoMapper.selectOne(queryWrapper);
        if (taskInfo == null) {
            log.info("新增屯币宝任务");
            taskInfo = new TaskInfo();
            taskInfo.setTaskState(TaskState.waiting);
            taskInfo.setCreatedAt(new Date());
            taskInfo.setUpdatedAt(taskInfo.getCreatedAt());
            taskInfo.setNextAt(taskInfo.getCreatedAt());
            taskInfo.setRunCount(0);
            taskInfo.setErrorCount(0);
            taskInfo.setIntervalSecond(60);
            taskInfo.setMaxError(10);
            taskInfo.setStrategy(Strategy.TUN_BI_BAO);

            BigDecimal init = new BigDecimal(1000);
            TunBiBaoData config = new TunBiBaoData();
            List<CurrencyHold> holdList = new ArrayList<>();
            holdList.add(newCurrencyHold(Currency.ETH, 0.33));
            holdList.add(newCurrencyHold(Currency.PEOPLE, 0.33));
            holdList.add(newCurrencyHold(Currency.BTC, 0.33));
            config.setCurrency(holdList);
            config.setRebalance(new BigDecimal(0.01));
            config.setInitUsdt(init);
            config.setCurrentUsdt(config.getInitUsdt());
            taskInfo.setData(JsonUtil.toJs(config));
            this.insert(taskInfo);
        }
        return taskInfo;
    }

    private CurrencyHold newCurrencyHold(Currency currency, double percent) {
        CurrencyHold hold = new CurrencyHold();
        hold.setCurrency(currency);
        hold.setHold(new BigDecimal(percent).multiply(new BigDecimal(100)));
        hold.setPercent(new BigDecimal(percent));
        return hold;
    }

    private void addRule(WangGeData wangGeData, Integer min, Integer max, Integer step, double quantity) {
        WangGeRule r = new WangGeRule();
        if (min != null) {
            r.setMin(new BigDecimal(min));
        }
        if (max != null) {
            r.setMax(new BigDecimal(max));
        }
        r.setStep(new BigDecimal(step));
        r.setQuantity(new BigDecimal(quantity).setScale(wangGeData.getCurrency().quantityScale(), RoundingMode.DOWN));
        wangGeData.getRules().add(r);
    }

    public TaskInfo getById(Long id) {
        return taskInfoMapper.selectById(id);
    }

    public void run(TaskInfo taskInfo) {
        taskInfo.setTaskState(TaskState.running);
        Strategy strategy = (Strategy) applicationContext.getBean(taskInfo.getStrategy());
        try {
            strategy.run(taskInfo);
            taskInfo.setUpdatedAt(new Date());
            taskInfo.setTaskState(TaskState.waiting);
            taskInfo.setRunCount(taskInfo.getRunCount() + 1);
            taskInfo.setRemark("ok");
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            String remark = LogUtil.getExceptionText(ex);
            if (remark.length() > 1000) {
                remark = remark.substring(0, 1000);
            }
            taskInfo.setErrorCount(taskInfo.getErrorCount() + 1);
            if (taskInfo.getErrorCount() > taskInfo.getMaxError()) {
                taskInfo.setTaskState(TaskState.stop);
            } else {
                taskInfo.setTaskState(TaskState.waiting);
            }
            taskInfo.setRemark(remark);
        }
        taskInfo.setRunCount(taskInfo.getRunCount() + 1);
        Date next = DateTime.current().addSecond(taskInfo.getIntervalSecond());
        taskInfo.setNextAt(next);
        this.update(taskInfo);
    }

    public TaskInfoVo syncTask(Long id) {
        TaskInfo taskInfo = getById(id);
        Strategy strategy = (Strategy) applicationContext.getBean(taskInfo.getStrategy());
        TaskInfo newTask = strategy.sync(taskInfo);
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("data", taskInfo.getData());
        updateWrapper.eq("id", taskInfo.getId());
        taskInfoMapper.update(null,updateWrapper);
        return toVo(newTask);
    }
}
