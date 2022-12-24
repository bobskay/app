package a.b.c.trace.controller;

import a.b.c.base.web.Response;
import a.b.c.trace.model.vo.RuleInfo;
import a.b.c.trace.service.UserConfigService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("rule")
public class RuleController {

    @Resource
    UserConfigService userConfigService;

    @PostMapping("get")
    public Response<RuleInfo> get(){
        return Response.success(userConfigService.getRuleInfo());
    }

    @PostMapping("update")
    public Response<RuleInfo> update(@RequestBody RuleInfo ruleInfo){
        userConfigService.updateRule(ruleInfo);
        return get();
    }

}
