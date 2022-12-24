package a.b.c.trace.controller;

import a.b.c.base.web.Response;
import a.b.c.trace.cache.UserInfoCache;
import a.b.c.trace.model.UserInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("test")
public class TestController {

    @Resource
    UserInfoCache userInfoCache;

    @RequestMapping("")
    public Response<Map> test(@RequestBody Map map){
        UserInfo info=userInfoCache.get("root");
        map.put("a",info);
        return Response.success(map);
    }
}
