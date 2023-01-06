package a.b.c.trace.job;

import a.b.c.trace.service.TraceOrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class TraceOrderCheckJob {

    @Resource
    TraceOrderService traceOrderService;

    @Scheduled(cron = "0 * * * * ? ")
    public void checkNew()  {
        traceOrderService.checkStatus();
    }
}
