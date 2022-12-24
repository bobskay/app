package a.b.c.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <消息处理中心>
 * <功能详细描述>
 *
 * @author wzh
 * @version 2018-07-24 23:11
 * @see [相关类/方法] (可选)
 **/
@Slf4j
@Component("webSocketHander")
public class WebSocketHander extends AbstractWebSocketHandler {
    private static Map<String ,WebSocketSession> sessionMap=new ConcurrentHashMap<>();
    // 服务器与客户端初次websocket连接成功执行
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("客户端连接服务器session id :" + session.getId());
        System.out.println(sessionMap);
        sessionMap.put(session.getId(),session);
    }

    // 接受消息处理消息
    @Override
    public void handleMessage(WebSocketSession webSocketSession,
                              WebSocketMessage<?> webSocketMessage)
            throws Exception {

        log.info("收到客户端消息:" + webSocketMessage.getPayload().toString());
        TextMessage message = new TextMessage("收到了:"+webSocketMessage.getPayload().toString());
        webSocketSession.sendMessage(message);

    }

    // 连接错误时触发
    @Override
    public void handleTransportError(WebSocketSession webSocketSession, Throwable throwable) throws Exception {
        if (webSocketSession.isOpen()) {
            webSocketSession.close();
        }

        log.debug("链接出错，关闭链接......");
    }

    // 关闭websocket时触发
    @Override
    public void afterConnectionClosed(WebSocketSession webSocketSession, CloseStatus closeStatus) throws Exception {
        log.debug("链接关闭......" + closeStatus.toString());
    }

    public void sendTo(String sessionId,String message) throws IOException {
        WebSocketSession session=sessionMap.get(sessionId);
        System.out.println(sessionMap);
        sessionMap.forEach((id,ss)->{
            System.out.println(id);
        });
        TextMessage textMessage = new TextMessage(message);
        session.sendMessage(textMessage);
    }

}