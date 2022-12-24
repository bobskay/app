package a.b.c.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    // 注册消息处理器，并映射连接地址
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册消息处理器，并添加自定义拦截器，支持websocket的连接访问
        registry.addHandler(new WebSocketHander(), "/spring/websocket")
                .addInterceptors(new WebSocketHandshakeInterceptor());
        registry.addHandler(new WebSocketHander(), "/spring/sockjs/websocket")
                .addInterceptors(new WebSocketHandshakeInterceptor()).withSockJS();
    }


}