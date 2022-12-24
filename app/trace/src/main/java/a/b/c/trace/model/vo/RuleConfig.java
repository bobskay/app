package a.b.c.trace.model.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class RuleConfig {

    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal quantity;
    private BigDecimal buySub;
    private BigDecimal sellAdd;
    private BigDecimal minProfit;
    private BigDecimal step;
    private Integer stepSecond;

    public RuleConfig(){

    }

    public RuleConfig(Double min, Double max, double quantity,
                      double buySub, double sellAdd, double minProfit,
                      double step, Integer stepSecond) {
        if (min != null) {
            this.min = newBigDecimal(min);
        }
        if (max != null) {
            this.max = newBigDecimal(max);
        }
        this.quantity = newBigDecimal(quantity);
        this.buySub = newBigDecimal(buySub);
        this.sellAdd = newBigDecimal(sellAdd);
        this.minProfit = newBigDecimal(minProfit);
        this.step = newBigDecimal(step);
        this.stepSecond = stepSecond;
    }

    private BigDecimal newBigDecimal(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }
}

