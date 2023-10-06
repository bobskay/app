package a.b.c.trace.controller;


import a.b.c.base.web.Response;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.trace.model.dto.TraceReportDto;
import a.b.c.trace.model.vo.TraceReportVo;
import a.b.c.trace.service.TraceInfoService;
import a.b.c.trace.service.WangGeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/traceReport")
@Slf4j
public class TraceReportController {


    @Resource
    TraceInfoService traceInfoService;

    @RequestMapping("list")
    public Response<List<TraceReportVo>> list(@RequestBody TraceReportDto traceReportDto){
        List<TraceReportVo> list=traceInfoService.report(traceReportDto);
        return Response.success(list);
    }
}
