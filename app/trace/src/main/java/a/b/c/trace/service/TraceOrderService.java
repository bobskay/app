package a.b.c.trace.service;

import a.b.c.base.service.BaseService;
import a.b.c.base.util.CollectionUtil;
import a.b.c.base.util.DateTime;
import a.b.c.base.util.IdWorker;
import a.b.c.base.util.StringUtil;
import a.b.c.base.vo.PageVo;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.dto.OpenOrder;
import a.b.c.exchange.enums.OrderSide;
import a.b.c.exchange.enums.OrderState;
import a.b.c.exchange.response.Order;
import a.b.c.trace.enums.Currency;
import a.b.c.trace.enums.TraceOrderType;
import a.b.c.trace.mapper.TraceOrderMapper;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.model.TraceOrder;
import a.b.c.trace.model.dto.TraceOrderDto;
import a.b.c.trace.model.vo.TaskInfoVo;
import a.b.c.trace.model.vo.TraceOrderVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TraceOrderService extends BaseService<TraceOrder> {


    @Resource
    TraceOrderMapper traceOrderMapper;
    @Resource
    Exchange exchange;
    @Resource
    TaskInfoService taskInfoService;

    public TraceOrder newOrder(Currency currency, Long businessId, String symbol,
                               BigDecimal price, BigDecimal quantity,String remark) {
        return this.insert(currency,businessId,symbol,price,quantity,OrderState.NEW,remark);
    }

    public TraceOrder filled(Currency currency, Long businessId, String symbol,
                             BigDecimal price, BigDecimal quantity,String remark) {
        return this.insert(currency,businessId,symbol,price,quantity,OrderState.FILLED,remark);
    }

    private TraceOrder insert(Currency currency, Long businessId, String symbol,
                            BigDecimal price, BigDecimal quantity,
                              OrderState orderState,String remark) {
        price = price.setScale(currency.scale(), RoundingMode.DOWN);
        quantity = quantity.setScale(currency.quantityScale(), RoundingMode.DOWN);

        TraceOrder traceOrder = new TraceOrder();
        traceOrder.setId(IdWorker.nextLong());
        if (quantity.compareTo(BigDecimal.ZERO) > 0) {
            traceOrder.setOrderSide(OrderSide.BUY);
        } else {
            traceOrder.setOrderSide(OrderSide.SELL);
        }
        traceOrder.setBusinessId(businessId);
        traceOrder.setOrderState(orderState.toString());
        traceOrder.setQuantity(quantity);
        traceOrder.setSymbol(symbol);
        traceOrder.setTraceOrderType(TraceOrderType.task);
        traceOrder.setExpectPrice(price);
        traceOrder.setCreatedAt(new Date());
        traceOrder.setUpdatedAt(traceOrder.getCreatedAt());
        traceOrder.setId(IdWorker.nextLong());
        traceOrder.setRemark(remark);
        if(orderState==OrderState.FILLED){
            traceOrder.setFinishAt(new Date());
        }
        String clientId = Long.toString(businessId, Character.MAX_RADIX) + "_" + traceOrder.getId();
        traceOrder.setClientOrderId(clientId);
        traceOrderMapper.insert(traceOrder);
        log.info("新增订单{}-{}:{},数量{}", clientId, symbol, price, quantity);
        return traceOrder;
    }

    public PageVo<TraceOrder> page(TraceOrderDto dto) {
        QueryWrapper wrapper = toWrapper(dto);
        //如果没传状态,就忽略取消的
        if (CollectionUtil.isEmpty(dto.getOrderStateList())) {
            wrapper.ne("order_state", OrderState.CANCELED.toString());
        }

        wrapper.orderByDesc("updated_at");

        IPage orderPage = traceOrderMapper.selectPage(dto.toPage(), wrapper);
        List<TraceOrderVo> voList=new ArrayList<>();
        List<Long> taskId=new ArrayList<>();
        List<Long> refId=new ArrayList<>();
        orderPage.getRecords().forEach(tOrder->{
            TraceOrder order=(TraceOrder) tOrder;
            if(TraceOrderType.task==order.getTraceOrderType()){
                taskId.add(order.getBusinessId());
            }
            if(order.getRefId()!=null){
                refId.add(order.getRefId());
            }
            TraceOrderVo vo=new TraceOrderVo();
            BeanUtils.copyProperties(order,vo);
            voList.add(vo);
        });

        Map<Long,TaskInfo> taskInfoMap=taskInfoService.taskInfoMap(taskId);
        List<TraceOrder> traceOrders = new ArrayList<>();
        if(refId.size()>0){
            traceOrders=traceOrderMapper.selectBatchIds(refId);
        }
        Map<Long,TraceOrder> orderMMap=CollectionUtil.toMap(traceOrders, TraceOrder::getId);

        voList.forEach(tracVo->{
            Long businessId=tracVo.getBusinessId();
            TraceOrder tr = orderMMap.get(tracVo.getRefId());
            if (tr != null) {
                tracVo.setRelatedOrder(tr);
            }
            TaskInfo taskInfo=taskInfoMap.get(businessId);
            if(taskInfo!=null){
                tracVo.setTaskInfo(taskInfo);
                tracVo.setBusinessType(taskInfo.getStrategy());
                return;
            }
        });


        //如果关联订单不能为空,说明查的是网格订单,网格订单状态用卖但
        if(dto.getRefIdNotNull()!=null && dto.getRefIdNotNull() && dto.getOrderStateList().contains(OrderState.FILLED.toString())){
                List<TraceOrderVo> filterSell=voList.stream()
                        .filter(vo-> vo.getSellEnd()!=null)
                        .collect(Collectors.toList());
                orderPage.setRecords(filterSell);
        }else{
            orderPage.setRecords(voList);
        }
        return new PageVo(orderPage);
    }

    //检查状态为new的订单,和远程同步状态
    public void checkStatus() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("order_state", OrderState.NEW);
        wrapper.le("created_at", DateTime.current().addMinute(-1));
        List<TraceOrder> list = traceOrderMapper.selectList(wrapper);
        for (TraceOrder order : list) {
            OpenOrder openOrder = exchange.openOrder(order.getClientOrderId(), order.getSymbol());
            if (order.getOrderState().equalsIgnoreCase(openOrder.getStatus())) {
                continue;
            }
            if(StringUtil.isNotEmpty(openOrder.getStatus())){
                order.setOrderState(openOrder.getStatus());
                updateState(order);
                continue;
            }
            //合约找不到到现货找
            Order sportOrder = exchange.getOrder(order.getSymbol(), order.getClientOrderId());
            if(order.getOrderState().equalsIgnoreCase(sportOrder.getStatus())){
                continue;
            }
            if (StringUtil.isNotEmpty(sportOrder.getStatus())) {
                order.setOrderState(sportOrder.getStatus());
                updateState(order);
                continue;
            }
            //1分钟了,合约和现货都没有,将状态改为cancel
            order.setOrderState(OrderState.CANCELED.toString());
            updateState(order);
        }
    }

    public TraceOrder getByClientOrderId(String clientOrderId) {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("client_order_id", clientOrderId);
        return traceOrderMapper.selectOne(wrapper);
    }

    public void updateState(TraceOrder order) {
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.set("order_state", order.getOrderState());
        updateWrapper.set("updated_at", new Date());
        if(OrderState.FILLED.toString().equalsIgnoreCase(order.getOrderState())){
            updateWrapper.set("finish_at",new Date());
        }
        updateWrapper.eq("id", order.getId());
        traceOrderMapper.update(null, updateWrapper);
    }

    public List<String> allSymbol() {
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.select("distinct symbol");
        List list=traceOrderMapper.selectObjs(wrapper);
        return list;
    }
}
