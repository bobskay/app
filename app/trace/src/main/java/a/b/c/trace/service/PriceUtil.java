package a.b.c.trace.service;

import a.b.c.trace.model.vo.RuleConfig;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.Date;

@Slf4j
public class PriceUtil {

    public static BigDecimal buySub(RuleConfig config,   Date buyTime){
        Integer diffSecond = (int) ((System.currentTimeMillis() - buyTime.getTime()) / 1000);
        int stepSecond=diffSecond/config.getStepSecond();
        BigDecimal buySub=config.getBuySub().subtract(config.getStep().multiply(new BigDecimal(stepSecond)));
        if(buySub.compareTo(config.getMinProfit())<0){
            buySub=config.getMinProfit();
        }
        log.debug("距离上次购买过去{}秒,买入价格提高{}*({}/{}),实际为减少{}",
                diffSecond,config.getStep(),diffSecond,config.getStepSecond(),buySub);
        return buySub;
    }

    public static BigDecimal sellAdd(RuleConfig config, Date sellStart) {
        Integer diffSecond = (int) ((System.currentTimeMillis() - sellStart.getTime()) / 1000);
        int stepSecond=diffSecond/config.getStepSecond();
        BigDecimal sellAdd=config.getSellAdd().subtract(config.getStep().multiply(new BigDecimal(stepSecond)));
        if(sellAdd.compareTo(config.getMinProfit())<0){
            sellAdd=config.getMinProfit();
        }
        log.debug("距离上次卖出过去{}秒,卖出价格减少{}*({}/{}),实际为减少{}",
                diffSecond,config.getStep(),diffSecond,config.getStepSecond(),sellAdd);
        return sellAdd;
    }
}
