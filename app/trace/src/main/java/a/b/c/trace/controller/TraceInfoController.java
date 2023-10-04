package a.b.c.trace.controller;

import a.b.c.base.vo.PageVo;
import a.b.c.base.web.Response;
import a.b.c.trace.model.TraceInfo;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.model.dto.TraceInfoDto;
import a.b.c.trace.model.dto.TraceOrderDto;
import a.b.c.trace.model.vo.TraceInfoVo;
import a.b.c.trace.service.TraceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/traceInfo")
@Slf4j
public class TraceInfoController {

    @Resource
    TraceInfoService traceInfoService;

    @RequestMapping("page")
    public Response<PageVo<TraceInfoVo>> page(@RequestBody @Valid TraceInfoDto traceInfoDto) {
        PageVo<TraceInfoVo>  page=traceInfoService.page(traceInfoDto);
        return Response.success(page);
    }
}
