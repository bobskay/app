package a.b.c.trace.cache;

import a.b.c.trace.mapper.UserInfoMapper;
import a.b.c.trace.model.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.function.Function;

@Component
public class UserInfoCache extends BaseCache<String, UserInfo> {

    @Resource
    UserInfoMapper userInfoMapper;

    @Override
    public UserInfo loadImpl(String userName) {
        QueryWrapper queryWrapper=new QueryWrapper();
        queryWrapper.eq("name",userName);
        return userInfoMapper.selectOne(queryWrapper);
    }
}
