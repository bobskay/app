package a.b.c.trace.component.strategy.vo;


import a.b.c.base.annotation.Remark;
import a.b.c.base.util.CollectionUtil;
import a.b.c.trace.enums.Currency;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Data
public class TunBiBaoData {
    private List<CurrencyHold> currency;
    @Remark("差额到达多少重新平衡,按总额的百分比")
    private BigDecimal rebalance;
    @Remark("初始资产")
    private BigDecimal initUsdt;
    @Remark("当前资产价格")
    private BigDecimal currentUsdt;

    public void setRebalance(BigDecimal rebalance){
        this.rebalance=rebalance.setScale(3, RoundingMode.DOWN);
    }

    public void setCurrentUsdt(BigDecimal currentUsdt){
        this.currentUsdt=currentUsdt.setScale(Currency.USDT.scale(), RoundingMode.DOWN);
    }

    public void setInitUsdt(BigDecimal initUsdt){
        this.initUsdt=initUsdt.setScale(Currency.USDT.scale(), RoundingMode.DOWN);
    }

    public BigDecimal getDiff(){
        if(currentUsdt==null|| CollectionUtils.isEmpty(currency)){
            return null;
        }

        BigDecimal total=currentUsdt;
        BigDecimal max = BigDecimal.ZERO;
        BigDecimal min = total;
        for (CurrencyHold hold :currency) {

            BigDecimal value = hold.getHold().multiply(hold.getPrice());
            if (value.compareTo(max) > 0) {
                max = value;
            }
            if (value.compareTo(min) < 0) {
                min = value;
            }
        }
        BigDecimal diffAmount = max.subtract(min).setScale(Currency.USDT.scale(), RoundingMode.DOWN);
        return diffAmount;
    }
}
