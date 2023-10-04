package a.b.c.trace.model;

import a.b.c.trace.enums.TraceState;
import lombok.Data;
import org.aspectj.weaver.tools.Trace;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TraceInfo {
    private Long id;
    private String buyId;
    private String sellId;
    private BigDecimal buyPrice;
    private BigDecimal sellPrice;
    private BigDecimal profit;
    private BigDecimal quantity;
    private Date buyStart;
    private Date sellStart;
    private Date sellEnd;
    private TraceState traceState;
}
