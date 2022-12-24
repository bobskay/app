package a.b.c.trace.model.vo;

import a.b.c.base.util.StringUtil;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.trace.model.AccountInfo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AccountVo extends AccountInfo {

    private List<OpenOrderVo> openOrders;
    private RuleConfig ruleConfig;
    private BigDecimal current;
    /**
     * 价格最高的买单
     * */
    public OpenOrderVo maxBuy() {
        OpenOrderVo buy=null;
        for(OpenOrderVo order:openOrders){
            if(!OrderSide.BUY.toString().equalsIgnoreCase(order.getSide())){
                continue;
            }
            if(buy==null || buy.getPrice().compareTo(order.getPrice())<0){
                buy=order;
            }
        }
        return buy;
    }

    /**
     * 价格最低的卖单
     * */
    public OpenOrderVo minSell() {
        OpenOrderVo sell=null;
        for(OpenOrderVo order:openOrders){
            if(!OrderSide.SELL.toString().equalsIgnoreCase(order.getSide())){
                continue;
            }
            if(sell==null || sell.getPrice().compareTo(order.getPrice())>0){
                sell=order;
            }
        }
        return sell;
    }

}
