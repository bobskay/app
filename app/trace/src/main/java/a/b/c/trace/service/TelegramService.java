package a.b.c.trace.service;

import a.b.c.base.util.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TelegramService {

    @Resource
    RestTemplate restTemplate;

    public void sendHtml(String html) throws Exception {
        String url="https://api.telegram.org/bot5949945863:AAGp2QaIhV9EaZyZdyFc5e6O9rlkDZi-iUc/sendMessage";
        Map map=new HashMap();
        map.put("chat_id","-602040449");
        map.put("text",html);
        map.put("parse_mode","html");
        String json = restTemplate.postForObject(url, getEntity(map), String.class);
        log.info("发送telegram返回："+json);
    }

    private HttpEntity getEntity(Object params) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON_UTF8);
        String json = JsonUtil.toJs(params);
        if (params == null || json == null) {
            HttpEntity<Object> entity = new HttpEntity<>(null, headers);
            return entity;
        }
        HttpEntity<Object> entity = new HttpEntity<>(json, headers);
        return entity;
    }
}
