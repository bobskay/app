package a.b.c.trace.component.strategy.vo;

import a.b.c.trace.model.ClimbOrder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ClimbData implements StrategyData {
    private BigDecimal lose = new BigDecimal("0.9");
    private List<ClimbStep> steps=new ArrayList<>();
    @JsonIgnore
    private List<ClimbOrder> orderList;
    private BigDecimal amount=new BigDecimal(100);
    private Integer max=10;


}
