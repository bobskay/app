package a.b.c.exchange.dto;

import a.b.c.base.util.json.JsonUtil;
import a.b.c.exchange.response.ApiResponse;
import a.b.c.exchange.response.ListResponse;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Prices   extends ApiResponse implements ListResponse {

    private List<Price> prices;

    @Override
    public void addResult(String response) {
        JSONArray array = JSONArray.parseArray(response);
        prices = new ArrayList<>();
        for (Object o : array) {
            Price price = JsonUtil.toBean(o.toString(), Price.class);
            prices.add(price);
        }
    }
}
