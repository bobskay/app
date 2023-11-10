package a.b.c.trace.cache;

import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.trace.mapper.UserInfoMapper;
import a.b.c.trace.model.UserInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class OpenOrderCache extends BaseCache<String, List<OpenOrder>> {

    @Resource
    Exchange exchange;

    @Override
    public List<OpenOrder> loadImpl(String symbol) {
        List<OpenOrder> order= exchange.openOrders(symbol);
        return new ArrayList<>(order);
    }
}
