package a.b.c;

import a.b.c.trace.enums.Currency;

import java.math.BigDecimal;

public class Constant {
    public static final String BASE_PACKAGE = Constant.class.getPackage().getName();
    public static final Integer MAX_QUERY_PAGE_SIZE = 100;
    public static final Integer DEFAULT_QUERY_PAGE_SIZE = 100;
    public static final String LINE="\n";
    public static final String SYMBOL= Currency.ETH.busd();

    public static final boolean CHECK_PERMISSION=false;

    //交易所开关
    public static final boolean DO_TRACE=true;


}
