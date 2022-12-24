package a.b.c.exchange.dto;

import a.b.c.base.util.json.JsonUtil;
import a.b.c.exchange.response.ApiResponse;
import a.b.c.exchange.response.ListResponse;
import com.alibaba.fastjson.JSONArray;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Assets  extends ApiResponse  implements ListResponse {

    private List<Asset> assets;

    @Override
    public void addResult(String response) {
        JSONArray array = JSONArray.parseArray(response);
        assets = new ArrayList<>();
        for (Object o : array) {
            Asset asset = JsonUtil.toBean(o.toString(), Asset.class);
            assets.add(asset);
        }
    }
}
