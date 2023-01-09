package a.b.c.trace.job;

import a.b.c.base.util.json.JsonUtil;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.Account;
import a.b.c.exchange.dto.Assets;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.trace.enums.Currency;
import a.b.c.trace.mapper.AccountHistoryMapper;
import a.b.c.trace.model.AccountHistory;
import a.b.c.trace.model.vo.AccountVo;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class AssetStatJob {

    @Resource
    Exchange exchange;
    @Resource
    AccountHistoryMapper accountHistoryMapper;

    @Scheduled(cron = "0 0 0/1 * * ? *")
    public void check() {
        Assets assets = exchange.assets();
        Account account = exchange.account(Currency.ETH.usdt());
        account.setAssets(account.getAssets().stream()
                .filter(ast -> ast.getWalletBalance().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList()));
        account.setPositions(account.getPositions().stream()
                .filter(pos -> pos.getPositionAmt().compareTo(BigDecimal.ZERO) != 0)
                .collect(Collectors.toList()));

        Map<String,String> map=new HashMap<>();
        assets.getAssets().forEach(asset -> {
            map.put(asset.getAsset(),asset.getFree().toString());
        });
        account.getAssets().forEach(assetsDTO -> {
            if(assetsDTO.getWalletBalance().compareTo(BigDecimal.ZERO)==0){
                return;
            }
            map.put(assetsDTO.getAsset(),assetsDTO.getMarginBalance());
        });
        AccountHistory accountHistory=new AccountHistory();
        accountHistory.setAccountInfo(JsonUtil.toJs(map));
        accountHistory.setCreatedAt(new Date());
        accountHistoryMapper.insert(accountHistory);
    }
}
