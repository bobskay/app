package a.b.c.exchange.socket.listener;


import com.alibaba.fastjson.JSONObject;

public interface AccountListener {

    boolean listen(JSONObject jsonObject);

    String eventName();
}
