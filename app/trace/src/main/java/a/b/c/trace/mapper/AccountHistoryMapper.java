package a.b.c.trace.mapper;

import a.b.c.trace.model.AccountHistory;
import a.b.c.trace.model.TaskInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountHistoryMapper extends BaseMapper<AccountHistory> {
}
