package a.b.c.trace.model.dto;

import a.b.c.base.dto.PageQueryDto;
import a.b.c.exchange.enums.OrderState;
import a.b.c.trace.enums.TraceState;
import a.b.c.trace.model.TraceInfo;
import a.b.c.trace.model.TraceOrder;
import lombok.Data;

import java.util.Date;

@Data
public class TraceInfoDto extends PageQueryDto<TraceInfo> {
    private Date buyStartStart;
    private Date buyStartEnd;
    private Date sellEndStart;
    private Date sellEndEnd;
    private TraceState traceState;
}
