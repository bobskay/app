package a.b.c.trace.controller;

import a.b.c.base.web.Response;
import a.b.c.trace.model.UserConfig;
import a.b.c.trace.model.vo.RuleConfig;
import a.b.c.trace.model.vo.RuleInfo;
import a.b.c.trace.service.UserConfigService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("userConfig")
public class UserConfigController {

    @Resource
    UserConfigService userConfigService;

    @PostMapping("getAll")
    public Response<List<UserConfig>> getAll(){
        List list=userConfigService.getAll();
        return Response.success(list);
    }

    @PostMapping("add")
    public Response<Integer> add(@RequestBody UserConfig userConfig){
        int update=userConfigService.add(userConfig);
        return Response.success(update);
    }


}
