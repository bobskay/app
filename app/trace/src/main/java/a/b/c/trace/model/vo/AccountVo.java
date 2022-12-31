package a.b.c.trace.model.vo;

import a.b.c.exchange.dto.Account;
import a.b.c.exchange.dto.Assets;
import a.b.c.exchange.dto.OpenOrder;
import lombok.Data;

import java.util.List;

@Data
public class AccountVo {
    private Assets assets;
    private Account account;
    private List<OpenOrder> openOrders;
}
