package a.b.c;

import java.math.BigDecimal;

public class Constant {
    public static final String BASE_PACKAGE = Constant.class.getPackage().getName();
    public static final Integer MAX_QUERY_PAGE_SIZE = 100;
    public static final Integer DEFAULT_QUERY_PAGE_SIZE = 100;
    public static final String LINE="\n";

    //交易所开关
    public static final boolean OPEN_SOCKET=true;
    public static final boolean DO_TRACE=true;

    public static final boolean CHECK_PERMISSION=false;

    //mock当前价格
    public static final boolean EXCHANGE_MOCK=!DO_TRACE;
    public static  BigDecimal MOCK_PRICE=new BigDecimal(1000);

}
