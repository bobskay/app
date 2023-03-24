package a.b.c.exchange;

import a.b.c.MarketConfig;
import a.b.c.base.util.DateTime;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.exchange.response.ExchangeInfo;
import a.b.c.trace.enums.Currency;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSONArray;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import a.b.c.exchange.dto.*;
import a.b.c.exchange.enums.*;
import a.b.c.exchange.response.Order;
import a.b.c.exchange.response.Ticker;
import a.b.c.exchange.utils.HttpClient;
import a.b.c.exchange.utils.OrderBuilder;
import a.b.c.exchange.utils.UrlParamsBuilder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class Exchange {
    private static ConcurrentHashMap<String, Exchange> exchangeMap = new ConcurrentHashMap<>();

    /**
     * @param symbol 默认交易对
     * @param scale  保留小数位数
     */
    public static Exchange getInstance(String symbol, Integer scale) {
        String key = symbol + scale;
        Exchange exchange = exchangeMap.get(key);
        if (exchange != null) {
            return exchange;
        }
        synchronized (Exchange.class) {
            exchange = exchangeMap.get(key);
            if (exchange != null) {
                return exchange;
            }
            exchange = new Exchange(symbol, scale);
            exchangeMap.put(key, exchange);
        }
        return exchange;
    }

    private HttpClient client;
    @Getter
    private String symbol;
    private Integer scale;

    private Exchange(String symbol, Integer scale) {
        client = new HttpClient();
        this.symbol = symbol;
        this.scale = scale;
    }

    public Ticker ticker() {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbol", symbol);
        return client.get(Api.TICKER, builder);
    }

    //账户信息
    public Account account(String symbol) {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbol", symbol);
        Account account = client.get(Api.ACCOUNT, builder);
        return account;
    }

    public boolean closeAll() {
        for (Account.PositionsDTO posi : this.account(symbol).getPositions()) {
            if (symbol.equalsIgnoreCase(posi.getSymbol())) {
                if (posi.getPositionAmt().compareTo(BigDecimal.ZERO) > 0) {
                    String orderId = OrderFlag.F + DateTime.current().toString(DateTime.Format.YEAR_TO_MILLISECOND_STRING);
                    this.order(OrderSide.SELL, BigDecimal.ZERO, posi.getPositionAmt(), orderId);
                    log.info("市价平仓：" + posi.getPositionAmt());
                    return true;
                }
                if (posi.getPositionAmt().compareTo(BigDecimal.ZERO) < 0) {
                    BigDecimal amount = BigDecimal.ZERO.subtract(posi.getPositionAmt());
                    String orderId = OrderFlag.F + DateTime.current().toString(DateTime.Format.YEAR_TO_MILLISECOND_STRING);
                    this.order(OrderSide.BUY, BigDecimal.ZERO, amount, orderId);
                    log.info("市价平仓：" + amount);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    public Balance balance() {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbol", symbol);
        return client.get(Api.BALANCE, builder);
    }

    public Price price() {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbol", symbol);
        return client.get(Api.PRICE, builder);
    }

    public String socketListenKey() {
        UserDataStream dataStream = client.invoke(Api.LISTEN_KEY, HttpUtil::createPost);
        return dataStream.getListenKey();
    }

    public void cancelAll() throws Exception {
        for (int i = 0; i < 5; i++) {
            UrlParamsBuilder builder = UrlParamsBuilder.build();
            builder.putToUrl("symbol", symbol);
            CancelAll cancelAll = client.delete(Api.CANCEL_ALL, builder);
            if (cancelAll.getCode() == 200) {
                return;
            }
            log.error("取消失败，准备重试:" + cancelAll);
            Thread.sleep(1000);
        }
        throw new RuntimeException("调用取消订单接口出错");
    }


    /**
     * @param price 下单价，如果为0说明是市价单
     */
    public Order order(OrderSide orderSide, BigDecimal price, BigDecimal quantity, String id) {
        if (MarketConfig.test) {
            log.error("测试环境{}假装下单:{}-{}", id + symbol, price, quantity);
            return null;
        }
        quantity = quantity.abs();
        BigDecimal newPrice = price.setScale(scale, RoundingMode.HALF_DOWN);
        if (newPrice.compareTo(price) != 0) {
            log.info("小数点过多，修改价格：" + price + "-->" + newPrice);
        }
        BigDecimal newQuantity = quantity.setScale(scale, RoundingMode.HALF_DOWN);
        if (newQuantity.compareTo(quantity) != 0) {
            log.info("小数点过多，修改数量：" + quantity + "-->" + newQuantity);
        }
        OrderDto orderDto = OrderBuilder.order(orderSide, newPrice, newQuantity);
        orderDto.setSymbol(symbol);
        orderDto.setNewClientOrderId(id);


        if (price.compareTo(BigDecimal.ZERO) == 0) {
            orderDto.setType(OrderType.MARKET);
            orderDto.setPrice(null);
            orderDto.setTimeInForce(null);
        }

        Order order = client.post(Api.ORDER, OrderBuilder.builder(orderDto));
        if (!OrderState.NEW.toString().equalsIgnoreCase(order.getStatus())) {
            log.info(order.getStatus()+"!="+OrderState.NEW.toString());
            log.error("下单失败,参数{}，返回{}", orderDto, order.getResponse());
        }
        return order;
    }

    public void cancel(String clientOrderId) {
        cancel(clientOrderId, symbol);
    }

    public void cancel(String clientOrderId, String symbol) {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("origClientOrderId", clientOrderId);
        builder.putToUrl("symbol", symbol);
        Cancel cancel = client.delete(Api.CANCEL, builder);
        if (!OrderState.CANCELED.toString().equalsIgnoreCase(cancel.getStatus())) {
            if (!cancel.getResponse().equalsIgnoreCase("{\"code\":-2011,\"msg\":\"Unknown order sent.\"}")) {
                log.info("取消失败：\n请求内容：{}\n返回结果：{}", cancel.getRequest(), cancel.getResponse());
            }
        }
    }

    public OpenOrder openOrder(String clientOrderId, String symbol) {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("origClientOrderId", clientOrderId);
        builder.putToUrl("symbol", symbol);
        return client.get(Api.OPEN_ORDER, builder);
    }

    public List<OpenOrder> openOrders() {
        return openOrders(symbol);
    }

    public List<OpenOrder> openOrders(String symbol) {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbol", symbol);
        OpenOrders orders = client.get(Api.OPEN_ORDERS, builder);
        return orders.getOpenOrders();
    }

    /**
     * [
     * [
     * 1607444700000,      // 开盘时间
     * "18879.99",         // 开盘价
     * "18900.00",         // 最高价
     * "18878.98",         // 最低价
     * "18896.13",         // 收盘价(当前K线未结束的即为最新价)
     * "492.363",          // 成交量
     * 1607444759999,      // 收盘时间
     * "9302145.66080",    // 成交额
     * 1874,               // 成交笔数
     * "385.983",          // 主动买入成交量
     * "7292402.33267",    // 主动买入成交额
     * "0"                 // 请忽略该参数
     * ]
     * ]
     */
    public List<Kline> kline(CandlestickInterval interval) {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbol", symbol);
        builder.putToUrl("interval", interval);
        List<Kline> lines = new ArrayList<>();
        client.invoke(Api.KLINE, builder, HttpUtil::createGet, resp -> {
            JSONArray jsonArray = JSONArray.parseArray(resp);
            for (Object o : jsonArray) {
                JSONArray arr = (JSONArray) o;
                Kline line = new Kline();
                line.setTime(new DateTime(arr.getLong(0), DateTime.Format.YEAR_TO_SECOND));
                line.setOpen(arr.getBigDecimal(1));
                line.setHigh(arr.getBigDecimal(2));
                line.setLow(arr.getBigDecimal(3));
                line.setClose(arr.getBigDecimal(4));
                lines.add(line);
            }
            return new Kline();
        });
        return lines;
    }


    public Map<String, BigDecimal> assetMap() {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        Assets assets = client.post(Api.ASSET, builder);
        if(assets.getAssets()==null){
            return new HashMap<>();
        }
        Map map = new LinkedHashMap();
        for (Asset as : assets.getAssets()) {
            map.put(as.getAsset(), as.getFree());
        }
        return map;
    }

    public Assets assets() {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        Assets assets = client.post(Api.ASSET, builder);
        return assets;
    }

    /**
     * 获取一批
     * */
    public Map<String, BigDecimal> getPrice(List<String> symbols) {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbols", JsonUtil.toJs(symbols));
        Prices prices = client.get(Api.PRICES, builder);
        Map<String, BigDecimal> map = new LinkedHashMap();
        for (Price p : prices.getPrices()) {
            map.put(p.getSymbol(), p.getPrice());
        }
        return map;
    }

    public ExchangeInfo exchangeInfo(){
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        ExchangeInfo info = client.get(Api.EXCHANGE_INFO, builder);
        return info;
    }

    public BigDecimal getPrice(String symbol) {
        UrlParamsBuilder builder = UrlParamsBuilder.build();
        builder.putToUrl("symbols", JsonUtil.toJs(Arrays.asList(symbol)));
        Prices prices = client.get(Api.PRICES, builder);
        Map<String, BigDecimal> map = new LinkedHashMap();
        for (Price p : prices.getPrices()) {
            map.put(p.getSymbol(), p.getPrice());
        }
        return map.get(symbol);
    }

    /**
     * 现货交易
     *
     * @param currency 要交易货币
     * @param quantity 交易数量,正数买负数卖
     * @param clientId 自定义订单号
     */
    public Order toUsdt(Currency currency, BigDecimal quantity, String clientId) {
        if (MarketConfig.test) {
            log.error("测试环境不下单,假装买入:" + currency + ":" + quantity);
            return null;
        }

        OrderSide orderSide = OrderSide.BUY;
        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            orderSide = OrderSide.SELL;
            quantity = BigDecimal.ZERO.subtract(quantity);
        }

        BigDecimal newQuantity = quantity.setScale(currency.getQuantityScale(), RoundingMode.HALF_UP);
        if (newQuantity.compareTo(quantity) != 0) {
            log.info("小数点过多，修改数量：" + quantity + "-->" + newQuantity);
        }

        OrderDto orderDto = OrderBuilder.order(orderSide, null, newQuantity);
        orderDto.setSymbol(currency.usdt());
        orderDto.setNewClientOrderId(clientId);
        orderDto.setTimeInForce(TimeInForce.GTC);

        orderDto.setType(OrderType.MARKET);
        Order order = client.post(Api.SPOT_ORDER, OrderBuilder.spotOrder(orderDto));
        if (order.getOrderId() == null) {
            log.error("toUsdt下单失败,参数{}，返回{}", orderDto, order.getResponse(), new Exception());
        }
        return order;
    }

    public Order getOrder(String symbol ,String origClientOrderId ){
        UrlParamsBuilder param= UrlParamsBuilder.build()
                .putToUrl("symbol",symbol)
                .putToUrl("origClientOrderId", origClientOrderId)
                .putToUrl("timestamp", System.currentTimeMillis());

        Order order = client.get(Api.GET_SPOT_ORDER, param);
        return order;
    }
}
