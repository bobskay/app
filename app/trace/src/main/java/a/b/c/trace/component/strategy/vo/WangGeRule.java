package a.b.c.trace.component.strategy.vo;

import a.b.c.base.annotation.Remark;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WangGeRule implements StrategyData{
    @Remark("可以匹配的最小价格,null表示不限制")
    private BigDecimal min;
    @Remark("可以匹配的最大价格,null表示不限制")
    private BigDecimal max;

    @Remark("买入减价,相对于最近一次的卖单")
    private BigDecimal buySub;

    @Remark("卖出加价,买入后加价多少卖出")
    private BigDecimal sellAdd;

    @Remark("每次交易数量")
    private BigDecimal quantity;
}
