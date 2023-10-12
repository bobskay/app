package a.b.c.trace.cache;

import a.b.c.Constant;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.Account;
import a.b.c.exchange.dto.OpenOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class HoldCache extends BaseCache<String, BigDecimal> {

    @Resource
    Exchange exchange;

    @Override
    public BigDecimal loadImpl(String symbol) {
        log.info("准备查询持仓:"+symbol);
        if(!Constant.DO_TRACE){
            return new BigDecimal(0);
        }

        for(Account.PositionsDTO positionsDTO:exchange.account(symbol).getPositions()){
            if(symbol.equalsIgnoreCase(positionsDTO.getSymbol())){
                return positionsDTO.getPositionAmt();
            }
        }
        return new BigDecimal(0);
    }

    protected long expireSecond(){
        return TimeUnit.MINUTES.toSeconds(1);
    }


}
