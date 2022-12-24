package a.b.c.trace.service;

import a.b.c.mock.MockUtil;
import a.b.c.trace.mapper.TraceOrderMapper;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.service.TraceOrderService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class TraceOrderServiceMock extends TraceOrderService {

    public TraceOrderServiceMock(){
        traceOrderMapper= MockUtil.getMock(TraceOrderMapper.class);
    }
}
