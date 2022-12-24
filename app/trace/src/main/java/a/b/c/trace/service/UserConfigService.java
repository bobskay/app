package a.b.c.trace.service;

import a.b.c.base.util.json.JsonUtil;
import a.b.c.trace.mapper.UserConfigMapper;
import a.b.c.trace.model.UserConfig;
import a.b.c.trace.model.vo.RuleConfig;
import a.b.c.trace.model.vo.RuleInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class UserConfigService {

    public int updateRule(RuleInfo ruleCInfo) {
        UserConfig userConfig=new UserConfig();
        userConfig.setContent(JsonUtil.toJs(ruleCInfo));
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("config_name", UserConfigEnum.RULE);
        return userConfigMapper.update(userConfig,wrapper);
    }

    public enum UserConfigEnum {
        RULE;
    }

    @Resource
    UserConfigMapper userConfigMapper;

    public List<UserConfig> getAll() {
        List<UserConfig> list = userConfigMapper.selectList(new QueryWrapper<>());
        Map<String, UserConfig> map = list.stream()
                .collect(Collectors.toMap(UserConfig::getConfigName, Function.identity()));
        return list;
    }

    public Integer add(UserConfig userConfig) {
        return userConfigMapper.insert(userConfig);
    }


    public RuleInfo getRuleInfo(){
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("config_name", UserConfigEnum.RULE);
        UserConfig config = userConfigMapper.selectOne(wrapper);
        if (config == null) {
            config = initRuleInfo();
        }
        RuleInfo info = JsonUtil.toBean(config.getContent(), RuleInfo.class);
        return info;
    }

    public RuleConfig getRuleConfig(BigDecimal hold) {
        RuleInfo info=getRuleInfo();
        for (RuleConfig cfg : info.getRuleConfigs()) {
            if (cfg.getMin() != null && cfg.getMin().compareTo(hold) >= 0) {
                continue;
            }
            if (cfg.getMax() != null && cfg.getMax().compareTo(hold) < 0) {
                continue;
            }
            return cfg;
        }
        throw new RuntimeException("找不到合适的配置" + hold);
    }

    private UserConfig initRuleInfo() {
        UserConfig userConfig = new UserConfig();
        userConfig.setConfigName(UserConfigEnum.RULE.toString());
        RuleInfo ruleInfo = new RuleInfo();
        ruleInfo.getRuleConfigs().add(new RuleConfig(null, 1D, 0.1, 10, 10, 5, 1, 60 * 10));
        ruleInfo.getRuleConfigs().add(new RuleConfig(1D, 2D, 0.1, 10, 10, 5, 1, 60 * 10));
        ruleInfo.getRuleConfigs().add(new RuleConfig(2D, 3D, 0.1, 10, 10, 5, 1, 60 * 10));
        ruleInfo.getRuleConfigs().add(new RuleConfig(3D, null, 0.1, 10, 10, 5, 1, 60 * 10));
        ruleInfo.setMaxHold(new BigDecimal(1));
        userConfig.setContent(JsonUtil.toJs(ruleInfo));
        userConfigMapper.insert(userConfig);
        return userConfig;
    }


}
