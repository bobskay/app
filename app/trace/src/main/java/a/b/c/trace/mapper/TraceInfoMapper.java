package a.b.c.trace.mapper;

import a.b.c.trace.model.TraceInfo;
import a.b.c.trace.model.dto.TraceReportDto;
import a.b.c.trace.model.vo.TraceReportVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TraceInfoMapper  extends BaseMapper<TraceInfo> {

    @Select(" SELECT sum(duration_seconds) durationSeconds, count(*) count,sum(profit) profit, DATE_FORMAT(sell_end, '%Y-%m-%d') time from trace_info " +
            "            where " +
            "        sell_end > #{start} and  sell_end < #{end} GROUP BY DATE_FORMAT(sell_end, '%Y-%m-%d')"+
            " order by DATE_FORMAT(sell_end, '%Y-%m-%d') desc"
    )
    List<TraceReportVo> dayReport(TraceReportDto traceReportDto);
}
