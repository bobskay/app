package a.b.c.trace.controller;

import a.b.c.base.Message;
import a.b.c.base.web.Response;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.trace.component.socket.listener.AggTradeListener;
import a.b.c.trace.model.AccountInfo;
import a.b.c.trace.model.dto.OpenOrderCancelDto;
import a.b.c.trace.model.dto.OpenOrderUpdateDto;
import a.b.c.trace.model.dto.OrderAddDto;
import a.b.c.trace.model.vo.AccountVo;
import a.b.c.trace.service.AccountInfoService;
import a.b.c.trace.service.MarketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.math.BigDecimal;

@RestController
@RequestMapping("accountInfo")
@Slf4j
public class AccountInfoController {

    @Resource
    AccountInfoService accountInfoService;
    @Resource
    AggTradeListener aggTradeListener;
    @Resource
    MarketService marketService;


    @RequestMapping("get")
    public Response<AccountVo> get() {
        AccountVo accountVo = accountInfoService.getAccount(aggTradeListener.getPrice());
        return Response.success(accountVo);
    }

    @RequestMapping("update")
    public Response<Integer> update(@RequestBody AccountInfo accountInfo) {
        int update = accountInfoService.update(accountInfo);
        return Response.success(update);
    }

//    @RequestMapping("updatePrice")
//    public Response<AccountVo> updatePrice(@RequestBody @Valid OpenOrderUpdateDto updateDto) {
//        OpenOrder order = exchange.openOrder(updateDto.getClientOrderId());
//        log.info("查询到的订单" + order);
//        if (order == null || order.getOrderId() == null) {
//            throw new Message("订单不存在:" + updateDto.getClientOrderId());
//        }
//        marketService.updatePrice(order, updateDto.getPrice());
//        return get();
//    }

    @RequestMapping("newBuy")
    public Response<AccountVo> newBuy(@RequestBody OrderAddDto orderAddDto) {
//        marketService.newBuy(orderAddDto.getPrice(), orderAddDto.getQuantity());
        return get();
    }

    @RequestMapping("cancel")
    public Response<AccountVo> cancel(@RequestBody @Valid OpenOrderCancelDto cancelDto) {
        marketService.cancel(cancelDto.getClientOrderId());
        return get();
    }

    @RequestMapping("currentPrice")
    public Response<BigDecimal> currentPrice() {
        return Response.success(aggTradeListener.getPrice());
    }
}
