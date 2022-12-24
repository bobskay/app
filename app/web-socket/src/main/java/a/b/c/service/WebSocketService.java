package a.b.c.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

/**
 * <基于javax websocket通讯>
 * <各个方法的参数都是可以根据项目的实际情况改的>
 *
 * @author wzh
 * @version 2018-07-08 17:11
 * @see [相关类/方法] (可选)
 **/
@ServerEndpoint(value = "/javax/websocket")
@Component
@Slf4j
public class WebSocketService {
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        // 如果是session没有激活的情况，就是没有请求获取或session，这里可能会取出空，需要实际业务处理
        HttpSession httpSession = (HttpSession) config.getUserProperties().get(HttpSession.class.getName());
        if (httpSession != null) {
            log.info("获取到httpsession" + httpSession.getId());
        } else {
            log.error("未获取到httpsession");
        }
        log.info("客户端连接服务器session id :" + session.getId());
    }

    @OnClose
    public void onClose(Session session) {
        log.info("session关闭:" + session.getId());
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        log.info("{}收到消息:", session.getId(), message);
        session.getBasicRemote().sendText( "收到" + session.getId() + "发的:"+message);
        new Thread(()->{
           for(int i=0;i<10;i++){
               try {
                   session.getBasicRemote().sendText(i+"");
                   Thread.sleep(1000);
               } catch (Exception e) {
                   log.error(e.getMessage(),e);
               }
           }
        }).start();
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("发生错误" + throwable.getMessage(), throwable);
    }
}