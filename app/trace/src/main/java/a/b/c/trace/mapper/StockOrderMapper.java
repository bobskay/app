package a.b.c.trace.mapper;

import a.b.c.trace.model.StockOrder;
import a.b.c.trace.model.UserConfig;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StockOrderMapper  extends BaseMapper<StockOrder> {


}