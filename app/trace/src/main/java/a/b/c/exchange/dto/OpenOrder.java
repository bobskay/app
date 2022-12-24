package a.b.c.exchange.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import a.b.c.exchange.response.ApiResponse;


import java.math.BigDecimal;
import java.util.Date;

@Data
@Slf4j
public class OpenOrder extends ApiResponse {


    private Long orderId;
    private String symbol;
    private String status;
    private String clientOrderId;
    private BigDecimal price;
    private String avgPrice;
    private BigDecimal origQty;
    private String executedQty;
    private String cumQuote;
    private String timeInForce;
    private String type;
    private Boolean reduceOnly;
    private Boolean closePosition;
    private String side;
    private String positionSide;
    private String stopPrice;
    private String workingType;
    private Boolean priceProtect;
    private String origType;
    private Date time;
    private Date updateTime;
}
