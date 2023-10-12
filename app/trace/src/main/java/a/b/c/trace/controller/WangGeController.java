package a.b.c.trace.controller;

import a.b.c.Constant;
import a.b.c.base.web.Response;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.exchange.dto.Price;
import a.b.c.trace.cache.ConfigCache;
import a.b.c.trace.component.socket.listener.AggTradeListener;
import a.b.c.trace.model.vo.WangGeVo;
import a.b.c.trace.service.ConfigService;
import a.b.c.trace.service.WangGeService;
import a.b.c.transaction.cache.ConfigInfo;
import a.b.c.transaction.cache.RunInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/wangGe")
@Slf4j
public class WangGeController {

    @Resource
    ConfigService configService;
    @Resource
    WangGeService wangGeService;
    @Resource
    Exchange exchange;
    @Resource
    AggTradeListener aggTradeListener;

    @RequestMapping("configInfo")
    public Response<ConfigInfo> configInfo() {
        return Response.success(configService.wangGeConfig());
    }

    @RequestMapping("runInfo")
    public Response<WangGeVo> runInfo() {
        WangGeVo vo=wangGeService.wangGeInfo();
        return Response.success(vo);
    }

    @RequestMapping("updateConfig")
    public Response<Boolean> updateConfig(@RequestBody @Valid ConfigInfo configInfo) {
        configService.updateByKey(ConfigCache.WANG_GE_CONFIG,configInfo);
        return Response.success(true);
    }

    @RequestMapping("openOrders")
    public Response<List<OpenOrder>> openOrders(){
        List<OpenOrder> list= wangGeService.openOrders();
        Collections.sort(list,(o1, o2) -> {
            return o1.getPrice().compareTo(o2.getPrice());
        });
        return Response.success(list);
    }


    @RequestMapping("mockPrice")
    public Response<Boolean> mockPrice(@RequestBody Price price){
        aggTradeListener.mockPrice(price.getPrice());
        return Response.success(true);
    }

    @RequestMapping("doBuy")
    public Response<Boolean> doBuy(){
        wangGeService.doBuy();
        return Response.success(true);
    }

    @RequestMapping("doTrace")
    public Response<Boolean> doTrace(){
        wangGeService.doTrace();
        return Response.success(true);
    }

    @RequestMapping("doFilled")
    public Response<Boolean> doFilled(@RequestBody OpenOrder openOrder){
        wangGeService.doFilled(openOrder.getClientOrderId());
        return Response.success(true);
    }
}


