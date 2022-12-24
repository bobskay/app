package a.b.c.trace.component.strategy.vo;

import a.b.c.base.annotation.Remark;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class WangGeRule {
    @Remark("可以匹配的最小价格,null表示不限制")
    private BigDecimal min;
    @Remark("可以匹配的最大价格,null表示不限制")
    private BigDecimal max;

    @Remark("每格变化价格")
    private BigDecimal step;

    @Remark("每次交易数量")
    private BigDecimal quantity;
}
