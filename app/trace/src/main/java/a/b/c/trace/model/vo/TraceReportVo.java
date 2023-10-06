package a.b.c.trace.model.vo;

import a.b.c.base.util.DateTime;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class TraceReportVo {
    private Date time;
    private Integer count;
    private BigDecimal profit;
    private Long durationSeconds;

    public String getAvgDuration() {
        if (count == null || durationSeconds == null) {
            return "";
        }
        long second = durationSeconds / count * 1000L;
        return DateTime.showHourTime(second);
    }
}

