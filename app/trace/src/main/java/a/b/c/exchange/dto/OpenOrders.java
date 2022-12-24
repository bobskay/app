package a.b.c.exchange.dto;

import a.b.c.base.util.json.JsonUtil;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;
import a.b.c.exchange.response.ApiResponse;
import a.b.c.exchange.response.ListResponse;

import java.util.ArrayList;
import java.util.List;

@Data
public class OpenOrders extends ApiResponse implements ListResponse {
    private List<OpenOrder> openOrders;

    @Override
    public void addResult(String response) {
        JSONArray array = JSONArray.parseArray(response);
        openOrders = new ArrayList<>();
        for (Object o : array) {
            OpenOrder openOrder = JsonUtil.toBean(o.toString(), OpenOrder.class);
            openOrders.add(openOrder);
        }
    }
}
