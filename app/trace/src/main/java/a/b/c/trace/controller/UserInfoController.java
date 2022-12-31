package a.b.c.trace.controller;

import a.b.c.base.web.Response;
import a.b.c.trace.mapper.UserInfoMapper;
import a.b.c.trace.model.UserInfo;
import a.b.c.trace.model.dto.UserListDto;
import a.b.c.trace.service.UserInfoService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.validation.Valid;

@RestController
@RequestMapping("userInfo")
public class UserInfoController {

    @Resource
    UserInfoMapper userMapper;
    @Resource
    UserInfoService userInfoService;

    //@PostMapping("list")
    public Response<IPage<UserInfo>> List(@RequestBody @Valid UserListDto userListDto){
        IPage<UserInfo> page = userInfoService.listPage(userListDto);
        return Response.success(page);
    }

    //@PostMapping("add")
    public Response<Integer> add(@RequestBody @Valid UserInfo userInfo){
        Integer added=userInfoService.add(userInfo);
        return Response.success(added);
    }
}
