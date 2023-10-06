package a.b.c.trace.job;

import a.b.c.Constant;
import a.b.c.base.util.log.LogUtil;
import a.b.c.trace.component.strategy.Strategy;
import a.b.c.trace.enums.TaskState;
import a.b.c.trace.model.TaskInfo;
import a.b.c.trace.service.TaskInfoService;
import a.b.c.trace.service.WangGeService;
import com.mysql.cj.util.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
@Slf4j
public class TaskJob {

    @Resource
    WangGeService wangGeService;
    @Resource
    ApplicationContext applicationContext;

    @Scheduled(cron = "0/10 * * * * ? ")
    public void doJob()  {
        if(Constant.DO_TRACE){
            wangGeService.doTrace();
        }
    }
}
