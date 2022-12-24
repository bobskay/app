package a.b.c.trace.service;

import a.b.c.trace.mapper.OrderInfoMapper;
import a.b.c.trace.model.OrderInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class OrderService {

    @Resource
    OrderInfoMapper orderInfoMapper;

    public OrderInfo getByCustomerNo(String customerNo) {
        QueryWrapper wrapper=new QueryWrapper<>();
        wrapper.eq("customer_no",customerNo);
        return orderInfoMapper.selectOne(wrapper);
    }
}
