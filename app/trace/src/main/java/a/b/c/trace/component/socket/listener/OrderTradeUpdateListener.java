package a.b.c.trace.component.socket.listener;

import a.b.c.exchange.socket.listener.AccountListener;
import a.b.c.trace.service.TraceService;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class OrderTradeUpdateListener implements AccountListener {

    @Resource
    TraceService traceService;

    @Override
    public boolean listen(JSONObject js) {
        try{
            traceService.orderUpdate(js);
        }catch (Exception ex){
            log.error("更新订单出错："+ex.getMessage(),ex);
        }
        return true;
    }

    public String eventName() {
        return "ORDER_TRADE_UPDATE";
    }

}
