package a.b.c.trace.controller;

import a.b.c.base.web.Response;
import a.b.c.trace.component.socket.listener.AggTradeListener;
import a.b.c.trace.model.AccountInfo;
import a.b.c.trace.model.vo.AccountVo;
import a.b.c.trace.service.AccountInfoService;
import a.b.c.trace.service.TraceService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("trace")
public class TraceController {

    @Resource
    TraceService traceService;
    @Resource
    AccountInfoService accountInfoService;
    @Resource
    AggTradeListener aggTradeListener;

    @RequestMapping("doTrace")
    public Response<AccountInfo> doTrace(){
        traceService.doTrace();
        AccountVo accountVo = accountInfoService.getAccount(aggTradeListener.getPrice());
        return Response.success(accountVo);
    }
}
