package a.b.c.exchange.socket.listener;

import com.alibaba.fastjson.JSONObject;
import a.b.c.exchange.enums.Stream;

public interface MessageListener extends AccountListener {
    boolean listen(JSONObject jsonObject);

    Stream stream();


}
