package a.b.c.trace.controller;

import a.b.c.base.util.CollectionUtil;
import a.b.c.base.web.Response;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.Account;
import a.b.c.exchange.dto.Assets;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.trace.enums.Currency;
import a.b.c.trace.model.vo.AccountVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("accountInfo")
public class AccountInfoController {

    @Resource
    Exchange exchange;

    @RequestMapping("get")
    public Response<AccountVo> get() {
        Assets assets = exchange.assets();
        assets.setResponse(null);

        Account account = exchange.account(Currency.ETH.usdt());
        account.setResponse(null);
        account.setAssets(account.getAssets().stream()
                .filter(ast -> ast.getWalletBalance().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList()));
        account.setPositions(account.getPositions().stream()
                .filter(pos -> pos.getPositionAmt().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList()));

        List<OpenOrder> openOrder = exchange.openOrders(null);
        Collections.sort(openOrder, (o1, o2) -> {
            if(o1.getSymbol().compareTo(o2.getSymbol())==0){
                return o2.getPrice().compareTo(o1.getPrice());
            }
            return o1.getSymbol().compareTo(o2.getSymbol());
        });
        AccountVo vo = new AccountVo();
        vo.setAssets(assets);
        vo.setAccount(account);
        vo.setOpenOrders(openOrder);
        return Response.success(vo);
    }

}

