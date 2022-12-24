package a.b.c.exchange.dto;

import a.b.c.base.annotation.Remark;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class Asset {

    @Remark("币种")
    private String asset;
    @Remark("可用")
    private BigDecimal free;
    private BigDecimal locked;
    private BigDecimal freeze;
    private BigDecimal btcValuation;

}
