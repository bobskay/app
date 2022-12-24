package a.b.c.trace.controller;

import a.b.c.base.web.Response;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.trace.model.dto.OpenOrderCancelDto;
import a.b.c.trace.model.vo.AccountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("/traceOrder")
@Slf4j
public class TraceOrderController {

    @Resource
    Exchange exchange;

    @RequestMapping("cancel")
    public Response<OpenOrder> cancel(@RequestBody @Valid OpenOrderCancelDto cancelDto) {
        exchange.cancel(cancelDto.getClientOrderId(), cancelDto.getSymbol());
        OpenOrder openOrder=exchange.openOrder(cancelDto.getClientOrderId(), cancelDto.getSymbol());
        return Response.success(openOrder);
    }
}
