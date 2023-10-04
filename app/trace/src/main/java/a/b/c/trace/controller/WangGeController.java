package a.b.c.trace.controller;

import a.b.c.base.vo.PageVo;
import a.b.c.base.web.Response;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.trace.model.dto.TraceInfoDto;
import a.b.c.trace.model.vo.TraceInfoVo;
import a.b.c.trace.service.WangGeService;
import a.b.c.transaction.cache.ConfigInfo;
import a.b.c.transaction.cache.RunInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/wangGe")
@Slf4j
public class WangGeController {

    @Resource
    WangGeService wangGeService;

    @RequestMapping("configInfo")
    public Response<ConfigInfo> configInfo() {
        return Response.success(wangGeService.getConfigInfo());
    }

    @RequestMapping("runInfo")
    public Response<RunInfo> runInfo() {
        return Response.success(wangGeService.getRunInfo());
    }

    @RequestMapping("updateConfig")
    public Response<ConfigInfo> updateConfig(@RequestBody @Valid ConfigInfo configInfo) {
        ConfigInfo old=wangGeService.getConfigInfo();
        BeanUtils.copyProperties(configInfo,old);
        return Response.success(old);
    }

    @RequestMapping("openOrders")
    public Response<List<OpenOrder>> openOrders(){
        List<OpenOrder> list= wangGeService.openOrders();
        return Response.success(list);
    }
}


