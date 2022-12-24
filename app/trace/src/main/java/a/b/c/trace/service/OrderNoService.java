package a.b.c.trace.service;

import a.b.c.base.util.DateTime;
import a.b.c.exchange.enums.OrderSide;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
public class OrderNoService {

    public String nextId() {
        return IdWorker.getIdStr();
    }
}
