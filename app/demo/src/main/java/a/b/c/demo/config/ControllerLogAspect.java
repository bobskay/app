package a.b.c.demo.config;


import a.b.c.base.annotation.Remark;
import a.b.c.base.utils.IdWorker;
import a.b.c.base.utils.LogUtil;
import a.b.c.web.utils.NameUtil;
import com.github.pagehelper.util.StringUtil;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;


/**
 * @author huawei
 * @date 2019-12-07
 */
@Aspect
@Slf4j
@Component
public class ControllerLogAspect {

  @Remark("缓存一下方法的url")
  private ConcurrentHashMap<String, String> methodUrlMap = new ConcurrentHashMap<>();

  /**
   * 对于所有返回类型是Response对象的方法,如果执行出错了就封装一下
   */
  @Pointcut("@within(org.springframework.web.bind.annotation.RestController)")
  private void responseMethod() {
  }

  @Around("responseMethod()")
  public Object process(ProceedingJoinPoint pjp) throws Throwable {
    try {
      MethodSignature msig = (MethodSignature) pjp.getSignature();
      String name = getName(methodUrlMap, msig.getMethod());
      LogUtil.setRequestId(getRequestId(name));
      Object[] args = pjp.getArgs();
      log.info("开始controller:{},参数:{}", name, Arrays.asList(args));
      return pjp.proceed();
    } finally {
      MDC.remove(LogUtil.REQUEST_ID);
    }
  }

  private String getRequestId(String name) {
    return IdWorker.nextString() + "-" + name;
  }

  public static String getName(ConcurrentHashMap<String, String> cacheMap, Method method) {
    String key = method.getDeclaringClass().getSimpleName() + "." + method.getName();
    String url = cacheMap.get(key);
    if (StringUtil.isNotEmpty(url)) {
      return url;
    }
    try {
      url = getUrlInner(method, key);
    } catch (Exception exception) {
      log.error("获取方法的url出错:" + key);
      return url;
    }
    cacheMap.put(key, url);
    return url;
  }

  private static String getUrlInner(Method method, String key) {
    String url;
    String controllerUrl = NameUtil.getUrl(method.getDeclaringClass());
    if (StringUtil.isNotEmpty(controllerUrl)) {
      if (!controllerUrl.startsWith("/")) {
        controllerUrl = "/" + controllerUrl;
      }
      if (controllerUrl.endsWith("/")) {
        controllerUrl = controllerUrl.substring(0, controllerUrl.length() - 1);
      }
    }

    String methodUrl = NameUtil.getUrl(method);
    if (StringUtil.isNotEmpty(methodUrl)) {
      if (!methodUrl.startsWith("/")) {
        methodUrl = "/" + methodUrl;
      }
      if (methodUrl.endsWith("/")) {
        methodUrl = controllerUrl.substring(0, controllerUrl.length() - 1);
      }
    }
    url = controllerUrl + methodUrl;
    if (StringUtil.isEmpty(url)) {
      log.error("找不到方法对应的url：" + key);
      url = key;
    }
    return url;
  }
}
