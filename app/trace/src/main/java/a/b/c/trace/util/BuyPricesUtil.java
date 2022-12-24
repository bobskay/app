package a.b.c.trace.util;

import a.b.c.base.util.StringUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BuyPricesUtil {
    public static final int MAX_LENGTH=1900;

    //最近的买单用逗号隔开,获取最尾部的
    public static BigDecimal lastBuy(String buyPrice) {
        if(StringUtil.isEmpty(buyPrice)){
            return null;
        }
        int pos=buyPrice.lastIndexOf(",");
        if(pos==-1){
            return new BigDecimal(buyPrice);
        }
        return new BigDecimal(buyPrice.substring(pos+1));
    }

    //如掉split后秒的字符
    public static String removeLast(String price) {
        if(price==null){
            return "";
        }
        int pos=price.lastIndexOf(",");
        if(pos==-1){
            return "";
        }
        return price.substring(0,pos);
    }


    public static String appendPrice(String buyPrices, BigDecimal price) {
        price=price.setScale(0, RoundingMode.HALF_UP);
        if(StringUtil.isEmpty(buyPrices)){
            return price.toString();
        }
        buyPrices= buyPrices+","+price;
        //超过最大长度了,把前面的删掉
        if(buyPrices.length()>MAX_LENGTH){
            int pos=buyPrices.indexOf(",");
            return buyPrices.substring(pos+1);
        }
        return buyPrices;
    }
}
