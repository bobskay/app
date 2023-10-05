package a.b.c.trace.service;

import a.b.c.base.util.IdWorker;
import a.b.c.base.util.json.JsonUtil;
import a.b.c.trace.cache.ConfigCache;
import a.b.c.trace.mapper.ConfigMapper;
import a.b.c.trace.model.Config;
import a.b.c.transaction.cache.ConfigInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ConfigService {


    @Resource
    ConfigMapper configMapper;
    @Resource
    ConfigCache configCache;

    public <T> T getByKey(String key, T defObject) {
        Config config = configCache.get(ConfigCache.WANG_GE_CONFIG);
        if (config == null) {
            config = new Config();
            config.setId(IdWorker.nextLong());
            config.setConfigKey(key);
            config.setConfigContent(JsonUtil.toJs(defObject));
            configMapper.insert(config);
        }
        return (T) JsonUtil.toBean(config.getConfigContent(), defObject.getClass());
    }

    public ConfigInfo wangGeConfig() {
        return getByKey(ConfigCache.WANG_GE_CONFIG,new ConfigInfo());
    }

    public void updateByKey(String key, Object value) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("config_key", key);
        Config config = new Config();
        config.setId(IdWorker.nextLong());
        config.setConfigKey(key);
        config.setConfigContent(JsonUtil.toJs(value));
        configMapper.update(config,wrapper);
        configCache.invalidate(key);
    }
}
