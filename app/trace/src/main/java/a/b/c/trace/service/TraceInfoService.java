package a.b.c.trace.service;

import a.b.c.base.service.BaseService;
import a.b.c.base.util.CollectionUtil;
import a.b.c.base.util.DateTime;
import a.b.c.base.vo.PageVo;
import a.b.c.exchange.enums.OrderState;
import a.b.c.trace.enums.TraceOrderType;
import a.b.c.trace.mapper.TraceInfoMapper;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceInfo;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.model.dto.TraceInfoDto;
import a.b.c.trace.model.dto.TraceOrderDto;
import a.b.c.trace.model.vo.TraceInfoVo;
import a.b.c.trace.model.vo.TraceOrderVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TraceInfoService extends BaseService<TraceInfo> {

    @Resource
    private TraceInfoMapper traceInfoMapper;

    public TraceInfo getByBuyId(String buyId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("buy_id", buyId);
        return traceInfoMapper.selectOne(wrapper);
    }

    public void insert(TraceInfo traceInfo) {
        traceInfoMapper.insert(traceInfo);
    }

    public void updateById(TraceInfo traceInfo) {
        traceInfoMapper.updateById(traceInfo);
    }

    public TraceInfo getBuSellId(String sellId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("sell_id", sellId);
        return traceInfoMapper.selectOne(wrapper);
    }

    public PageVo<TraceInfoVo> page(TraceInfoDto dto) {
        QueryWrapper wrapper = toWrapper(dto);
        wrapper.orderByDesc("buy_start");
        IPage orderPage = traceInfoMapper.selectPage(dto.toPage(), wrapper);
        List<TraceInfoVo> voList = new ArrayList<>();
        orderPage.getRecords().forEach(tOrder -> {
            TraceInfo order = (TraceInfo) tOrder;
            TraceInfoVo vo = new TraceInfoVo();
            BeanUtils.copyProperties(order, vo);
            voList.add(vo);
        });
        orderPage.setRecords(voList);
        return new PageVo(orderPage);
    }
}

