package a.b.c.trace.controller;

import a.b.c.base.vo.PageVo;
import a.b.c.base.web.Response;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.model.dto.OpenOrderCancelDto;
import a.b.c.trace.model.dto.TraceOrderDto;
import a.b.c.trace.service.TraceOrderService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/traceOrder")
@Slf4j
public class TraceOrderController {

    @Resource
    Exchange exchange;
    @Resource
    TraceOrderService traceOrderService;

    @RequestMapping("cancel")
    public Response<OpenOrder> cancel(@RequestBody @Valid OpenOrderCancelDto cancelDto) {
        exchange.cancel(cancelDto.getClientOrderId(), cancelDto.getSymbol());
        OpenOrder openOrder=exchange.openOrder(cancelDto.getClientOrderId(), cancelDto.getSymbol());
        return Response.success(openOrder);
    }

    @RequestMapping("page")
    public Response<PageVo<TraceOrder>> page(@RequestBody @Valid TraceOrderDto traceOrderDto) {
        PageVo<TraceOrder>  page=traceOrderService.page(traceOrderDto);
        return Response.success(page);
    }

    @RequestMapping("openOrder")
    public Response<OpenOrder> openOrder(@RequestBody @Valid TraceOrderDto traceOrderDto) {
        OpenOrder openOrder=exchange.openOrder(traceOrderDto.getClientOrderId(),traceOrderDto.getSymbol());
        return Response.success(openOrder);
    }



}
