package a.b.c.controller;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.socket.TextMessage;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @SendTo("/topicTest/hello")
    @RequestMapping("hello")
    public String hello(){
        messagingTemplate.convertAndSend("/topicTest/hello","广播啦");
       return "success";
    }
}
