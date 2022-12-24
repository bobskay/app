package a.b.c.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import javax.servlet.http.HttpSession;
import java.util.Map;


@Slf4j
public class WebSocketHandshakeInterceptor extends HttpSessionHandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request,
                                   ServerHttpResponse response,
                                   WebSocketHandler webSocketHandler, Map<String, Object> map)
            throws Exception {
        // websocket握手建立前调用，获取httpsession
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequset = (ServletServerHttpRequest) request;
            HttpSession httpSession = servletRequset.getServletRequest().getSession(false);
            if (httpSession != null) {
                log.info("httpSession key：" + httpSession.getId());
                map.put("HTTP_SESSION", httpSession);
            } else {
                log.warn("httpSession is null");
            }
        }
        return super.beforeHandshake(request, response, webSocketHandler, map);
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest,
                               ServerHttpResponse serverHttpResponse,
                               WebSocketHandler webSocketHandler, Exception e) {
        // websocket握手建立后调用
        log.info("websocket连接握手成功");
    }
}
