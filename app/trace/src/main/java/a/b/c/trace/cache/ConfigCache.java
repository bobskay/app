package a.b.c.trace.cache;

import a.b.c.base.util.json.JsonUtil;
import a.b.c.trace.mapper.ConfigMapper;
import a.b.c.trace.mapper.UserInfoMapper;
import a.b.c.trace.model.Config;
import a.b.c.trace.model.UserInfo;
import a.b.c.trace.service.ConfigService;
import a.b.c.trace.service.WangGeService;
import a.b.c.transaction.cache.ConfigInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class ConfigCache extends BaseCache<String, Config> {
    public static final String WANG_GE_CONFIG="wangGeConfig";

    @Resource
    ConfigMapper configMapper;

    @Override
    public Config loadImpl(String key) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("config_key", key);
        return configMapper.selectOne(wrapper);
    }
}
