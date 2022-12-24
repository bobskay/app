package a.b.c.trace.service;

import a.b.c.exchange.dto.OpenOrder;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.trace.component.socket.listener.AggTradeListener;
import a.b.c.trace.mapper.AccountInfoMapper;
import a.b.c.trace.model.AccountInfo;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.Account;
import a.b.c.trace.model.StockOrder;
import a.b.c.trace.model.vo.AccountVo;
import a.b.c.trace.model.vo.OpenOrderVo;
import a.b.c.trace.model.vo.RuleConfig;
import a.b.c.trace.util.BuyPricesUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
@Slf4j
public class AccountInfoService {

    @Resource
    AccountInfoMapper accountInfoMapper;
    @Resource
    Exchange exchange;
    @Resource
    AggTradeListener aggTradeListener;
    @Resource
    UserConfigService userConfigService;

    public AccountVo getAccount(BigDecimal currentPrice) {
        BigDecimal hold = BigDecimal.ZERO;
        Account account = exchange.account(exchange.getSymbol());
        for (Account.PositionsDTO positionsDTO : account.getPositions()) {
            if (positionsDTO.getSymbol().equalsIgnoreCase(exchange.getSymbol())) {
                //持有
                hold = positionsDTO.getPositionAmt();
                break;
            }
        }
        QueryWrapper query = new QueryWrapper();
        query.eq("currency", exchange.getSymbol());
        AccountInfo accountInfo = accountInfoMapper.selectOne(query);
        if (accountInfo == null) {
            accountInfo = new AccountInfo();
            accountInfo.setHold(hold);
            accountInfo.setCurrency(exchange.getSymbol());
            accountInfo.setLastBuy(new Date());
            accountInfo.setLastSell(new Date());
            accountInfo.setAvgPrice(currentPrice);
            accountInfo.setConfirmPrice(currentPrice);
            accountInfoMapper.insert(accountInfo);
        }

        if(accountInfo.getHold().compareTo(hold)!=0){
            accountInfo.setHold(hold);
            accountInfoMapper.updateById(accountInfo);
        }
        AccountVo accountVo = new AccountVo();
        BeanUtils.copyProperties(accountInfo, accountVo);

        List<OpenOrder> openOrders = exchange.openOrders();
        List<OpenOrderVo> vos=new ArrayList<>();
        RuleConfig rule = userConfigService.getRuleConfig(accountVo.getHold());
        accountVo.setRuleConfig(rule);
        openOrders.forEach(openOrder -> {
            OpenOrderVo vo=new OpenOrderVo();
            vo.setBasePrice(accountVo.getConfirmPrice().setScale(2,RoundingMode.HALF_UP));
            BeanUtils.copyProperties(openOrder,vo);
            if(vo.getSide().equalsIgnoreCase(OrderSide.BUY.toString())){
                vo.setHoldSecond((System.currentTimeMillis()-accountVo.getLastBuy().getTime())/1000);
                BigDecimal diff=new BigDecimal(vo.getHoldSecond()/rule.getStepSecond());
                BigDecimal diffPrice=rule.getBuySub().subtract(diff.multiply(rule.getStep()));
                if(diffPrice.compareTo(rule.getMinProfit())<0){
                    diffPrice=rule.getMinProfit();
                }
                BigDecimal expectPrice=vo.getBasePrice().subtract(diffPrice).setScale(2,RoundingMode.HALF_UP);
                vo.setExpectPriceDesc(vo.getBasePrice()+"-"+diffPrice+"="+expectPrice);
                vo.setExpectPrice(expectPrice);
            }else{
                vo.setHoldSecond((System.currentTimeMillis()-accountVo.getLastSell().getTime())/1000);
                BigDecimal diff=new BigDecimal(vo.getHoldSecond()/rule.getStepSecond());
                BigDecimal diffPrice=rule.getSellAdd().subtract(diff.multiply(rule.getStep()));
                if(diffPrice.compareTo(rule.getMinProfit())<0){
                    diffPrice=rule.getMinProfit();
                }
                BigDecimal expect=vo.getBasePrice().add(diffPrice).setScale(2,RoundingMode.HALF_UP);
                String expectPriceDesc=vo.getBasePrice()+"+"+diffPrice+"="+expect;

                //最近一笔买单的价格
                BigDecimal lastBuy=BuyPricesUtil.lastBuy(accountVo.getBuyPrices());
                if(lastBuy==null){
                    lastBuy=accountVo.getAvgPrice();
                }
                //最近买入价加上最小利润
                BigDecimal lowest=lastBuy.add(rule.getMinProfit());
                if(expect.compareTo(lowest)<0){
                    vo.setExpectPriceDesc(expectPriceDesc+"=>"+lowest);
                    vo.setExpectPrice(lowest);
                }else{
                    vo.setExpectPriceDesc(expectPriceDesc);
                    vo.setExpectPrice(expect);
                }
            }

            vos.add(vo);
        });
        Collections.sort(vos, Comparator.comparing(OpenOrder::getPrice));
        accountVo.setCurrent(currentPrice);
        accountVo.setOpenOrders(vos);
        if(accountVo.getBuyPrices()==null){
            accountVo.setBuyPrices("");
        }
        return accountVo;
    }

    public void updateTrace(AccountInfo accountInfo) {
        accountInfoMapper.updateById(accountInfo);
    }

    public int update(AccountInfo accountInfo) {
        return accountInfoMapper.updateById(accountInfo);
    }

    /**
     * 买单成交
     * */
    public void buyFinish(StockOrder order) {
        BigDecimal current=aggTradeListener.getPrice();
        if(current==null){
            current=order.getPrice();
        }
        AccountVo accountVo=getAccount(current);
        accountVo.setLastBuy(new Date());

        BigDecimal total=accountVo.getAvgPrice().multiply(accountVo.getHold()).add(order.getQuantity().multiply(order.getPrice()));
        accountVo.setHold(accountVo.getHold().add(order.getQuantity()).setScale(2, RoundingMode.HALF_UP));
        BigDecimal avg=total.divide(accountVo.getHold(),2,RoundingMode.HALF_UP);
        accountVo.setAvgPrice(avg);
        accountVo.setConfirmPrice(order.getPrice());
        accountVo.setBuyPrices(BuyPricesUtil.appendPrice(accountVo.getBuyPrices(),order.getPrice()));
        accountInfoMapper.updateById(accountVo);
    }

    public void sellFinish(StockOrder order) {
        BigDecimal current=aggTradeListener.getPrice();
        if(current==null){
            current=order.getPrice();
        }
        AccountVo accountVo=getAccount(current);
        accountVo.setLastSell(new Date());
        accountVo.setHold(accountVo.getHold().subtract(order.getQuantity()).setScale(2, RoundingMode.HALF_UP));
        accountVo.setConfirmPrice(order.getPrice());
        accountVo.setBuyPrices(BuyPricesUtil.removeLast(accountVo.getBuyPrices()));
        accountInfoMapper.updateById(accountVo);
    }


}
