package a.b.c.trace.component.socket.listener;

import a.b.c.exchange.socket.listener.MessageListener;
import com.alibaba.fastjson.JSONObject;
import a.b.c.exchange.enums.Stream;
import org.springframework.stereotype.Component;

@Component
public class KlineListener implements MessageListener {
    private Stream stream = Stream.kline_1m;


    @Override
    public boolean listen(JSONObject js){
        return true;
    }

    @Override
    public String eventName() {
      return "kline";
    }


    @Override
    public Stream stream() {
        return stream;
    }

}
