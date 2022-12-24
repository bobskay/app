package a.b.c.exchange;

import a.b.c.Constant;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.exchange.dto.Account;
import a.b.c.exchange.dto.Assets;
import a.b.c.exchange.dto.Price;
import a.b.c.exchange.dto.Prices;
import a.b.c.trace.enums.Currency;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ExchangeTest {

    public static void main(String[] args) {
        Exchange exchange=Exchange.getInstance(Currency.ETH.usdt(), 2);
        Account account=exchange.account(exchange.getSymbol());
        System.out.println(account);
    }

   @Test
    public void account() {
       Exchange exchange=Exchange.getInstance(Currency.ETH.usdt(), 2);
        Account account=exchange.account(exchange.getSymbol());
        account.setResponse(null);
        System.out.println(JsonUtil.prettyJs(account));
    }

    @Test
    public void getAssets() {
        Exchange exchange=Exchange.getInstance(Currency.ETH.usdt(), 2);
        Map asset=exchange.getAssets();
        System.out.println(JsonUtil.prettyJs(asset));
    }

    @Test
    public void getPrice() {
        Exchange exchange=Exchange.getInstance(Currency.ETH.usdt(), 2);
        List<String> symbols=new ArrayList<>();
        symbols.add(Currency.PEOPLE.usdt());
        symbols.add(Currency.ETH.usdt());
        Map<String, BigDecimal> prices=exchange.getPrice(symbols);
        System.out.println(prices);
    }
}