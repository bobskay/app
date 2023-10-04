package a.b.c.transaction.utils;

import a.b.c.base.util.DateTime;

import java.math.BigDecimal;
import java.util.Date;

public class OrderIdUtil {
    public static final String BUY="B";
    public static final String SELL="S";

    public  static String buy(BigDecimal price,BigDecimal quantity){
        String time= DateTime.toString(new Date(), DateTime.Format.YEAR_TO_SECOND_STRING);
        return BUY+"_"+price+"_"+quantity+"_"+time;
    }

    public static String sell(BigDecimal price, BigDecimal quantity) {
        String time= DateTime.toString(new Date(), DateTime.Format.YEAR_TO_SECOND_STRING);
        return SELL+"_"+price+"_"+quantity+"_"+time;
    }
}
