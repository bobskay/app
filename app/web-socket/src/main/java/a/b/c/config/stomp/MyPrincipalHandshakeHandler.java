package a.b.c.config.stomp;


import a.b.c.config.stomp.WebSocketUserAuthentication;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.Map;

@Slf4j
@Data
public class MyPrincipalHandshakeHandler extends DefaultHandshakeHandler{


    @Override
    protected Principal determineUser(ServerHttpRequest request, WebSocketHandler wsHandler, Map<String, Object> attributes) {
        HttpSession httpSession = getSession(request);
        String user = (String)httpSession.getAttribute("loginName");
        if(user==null){
            log.error("未登录系统，禁止登录websocket!");
            return null;
        }
        log.info(" 登录用户 login = " + user);
        return new WebSocketUserAuthentication(user);
    }

    private HttpSession getSession(ServerHttpRequest request) {
        ServletServerHttpRequest serverRequest = (ServletServerHttpRequest) request;
        if (request instanceof ServletServerHttpRequest) {
            return serverRequest.getServletRequest().getSession(true);
        }
        return serverRequest.getServletRequest().getSession(true);
    }

}