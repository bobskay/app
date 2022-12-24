package a.b.c.trace.model.vo;

import a.b.c.base.annotation.Remark;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class RuleInfo {
    @Remark("最大持仓")
    private BigDecimal maxHold;
    private List<RuleConfig> ruleConfigs=new ArrayList<>();
}
