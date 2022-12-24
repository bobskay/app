package a.b.c.trace.service;

import a.b.c.base.service.BaseService;
import a.b.c.trace.mapper.UserInfoMapper;
import a.b.c.trace.model.UserInfo;
import a.b.c.trace.model.dto.UserListDto;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
public class UserInfoService extends BaseService {


    @Resource
    UserInfoMapper userInfoMapper;

    @Transactional
    public Integer add(UserInfo userInfo) {
        return userInfoMapper.insert(userInfo);
    }

    public IPage<UserInfo> listPage(UserListDto dto) {
        QueryWrapper wrapper = toWrapper(dto);
        IPage<UserInfo> userInfoIPage = userInfoMapper.selectPage(dto.toPage(), wrapper);
        return userInfoIPage;
    }

}
