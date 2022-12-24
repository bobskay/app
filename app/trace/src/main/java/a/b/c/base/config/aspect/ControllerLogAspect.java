package a.b.c.base.config.aspect;

import a.b.c.base.util.IdWorker;
import a.b.c.base.util.json.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Aspect
@Component
@Slf4j
public class ControllerLogAspect {
    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void pointcut() {
    }

    //配置环绕通知,使用在方法aspect()上注册的切入点
    @Around(value = "pointcut()", argNames = "joinPoint")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MDC.put("traceId", IdWorker.nextString());
        ServletRequestAttributes attributes= ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes());
        if(attributes==null){
            return joinPoint.proceed();
        }
        HttpServletRequest request=attributes.getRequest();
        if(request==null){
            return joinPoint.proceed();
        }
        long begin=System.currentTimeMillis();
        String arg;
        List list=new ArrayList<>();
        try{
            for(Object o:joinPoint.getArgs()){
                list.add(o);
            }
            arg= JsonUtil.PRETTY_MAPPER.writeValueAsString(list);
        }catch (Throwable ex){
            log.error("打印参数出错：",ex);
            arg=list.toString();
        }
        log.debug("开始请求："+request.getRequestURL()+":"+arg );
        try{
            return joinPoint.proceed();
        }finally {
            log.debug("结束请求："+request.getRequestURL()+"耗时："+(System.currentTimeMillis()-begin));
            MDC.remove("traceId");
        }
    }
}
