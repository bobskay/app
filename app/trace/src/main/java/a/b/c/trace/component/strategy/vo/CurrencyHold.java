package a.b.c.trace.component.strategy.vo;

import a.b.c.base.annotation.Remark;
import a.b.c.trace.enums.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class CurrencyHold {

    @Remark("币种")
    private Currency currency;
    @Remark("持仓")
    private BigDecimal hold;
    @Remark("占比,0.33")
    private BigDecimal percent;
    @Remark("转为usdt后的价格")
    private BigDecimal usdt;
    @Remark("转为usdt的价格")
    private BigDecimal price;

    public void setPercent(BigDecimal percent){
        this.percent=percent.setScale(4, RoundingMode.DOWN);
    }

    public void setUsdt(BigDecimal usdt){
        if(usdt==null){
            return;
        }
        this.usdt=usdt.setScale(Currency.USDT.getQuantityScale(), RoundingMode.DOWN);
    }
}
