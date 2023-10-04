package a.b.c.trace.component.socket;

import a.b.c.Constant;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.socket.listener.AccountListener;
import a.b.c.exchange.socket.listener.MessageListener;
import lombok.extern.slf4j.Slf4j;


import java.net.URISyntaxException;
import java.util.List;

@Slf4j
public class SocketClient {

    private MyWebSocketClient client;
    private List<MessageListener> messageListeners;
    private List<AccountListener> accountListeners;
    private Exchange exchange;

    public SocketClient(Exchange exchange, List<MessageListener> messageListeners, List<AccountListener> accountListeners) throws URISyntaxException, InterruptedException {
       this.exchange=exchange;
       this.messageListeners=messageListeners;
       this.accountListeners=accountListeners;
       if(Constant.OPEN_SOCKET){
           createClient();
       }
    }

    public void createClient() throws URISyntaxException, InterruptedException {
        if(client!=null){
            client.close();
        }
        log.info("准备创建socket");
        client = MyWebSocketClient.getInstance(exchange, messageListeners, accountListeners);
        client.start();
    }

}
