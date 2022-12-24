package a.b.c.controller;


import a.b.c.config.WebSocketHander;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;


/**
 * <websocket测试用MVC控制器>
 * <功能详细描述>
 * @author wzh
 * @version 2018-07-09 22:53
 * @see [相关类/方法] (可选)
 **/
@RestController
@RequestMapping("/websocket")
@Slf4j
public class WebSocketController {
    @Resource
    private WebSocketHander webSocketHander;

    // 测试私信发送
    @RequestMapping("/send")
    public void send(@RequestParam String id) throws IOException {
        log.info(id);
        webSocketHander.sendTo(id,"hello");
    }
}