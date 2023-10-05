package a.b.c.trace.mapper;

import a.b.c.trace.model.AccountHistory;
import a.b.c.trace.model.Config;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigMapper extends BaseMapper<Config> {
}
