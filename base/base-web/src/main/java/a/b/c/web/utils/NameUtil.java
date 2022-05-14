package a.b.c.web.utils;


import a.b.c.base.annotation.Remark;
import a.b.c.base.annotation.See;
import a.b.c.base.utils.StringUtil;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
public class NameUtil {

  public static String getName(Field field) {
    return field.getDeclaringClass().getName() + "." + field.getName();
  }

  public static String getRemark(Class clazz) {
    Remark remark= (Remark) clazz.getAnnotation(Remark.class);
    if(remark!=null){
      return remark.value();
    }
    return clazz.getSimpleName();
  }

  public static String getRemark(Field field) {
    See see = field.getAnnotation(See.class);

    Remark remark = field.getAnnotation(Remark.class);
    if (remark != null) {
      return addSee(remark.value(), see);
    }

    List<Class> clazz = new ArrayList<>();
    clazz.add(NotNull.class);
    clazz.add(NotBlank.class);
    clazz.add(Pattern.class);
    clazz.add(NotEmpty.class);
    clazz.add(Size.class);

    for (Class cls : clazz) {
      String message = getValidateRemark(see, field, cls);
      if (message != null) {
        return message;
      }
    }
    return addSee("", see);
  }

  /**
   * 获得数据校验注解传的message值
   *
   * @param annotationClass 注解类，必须有message方法
   */
  private static String getValidateRemark(See see, Field field, Class annotationClass) {
    Annotation annotation = field.getAnnotation(annotationClass);
    if (annotation != null) {
      try {
        String message = (String) annotationClass.getMethod("message").invoke(annotation);
        return addSee(validateName(message, field), see);
      } catch (Exception e) {
        log.error("获取message出错：" + e.getMessage(), e);
      }
    }
    return null;
  }

  private static String addSee(String value, See see) {
    String enumStr = createEnumRemark(see);

    if (StringUtil.isEmpty(value)) {
      return enumStr;
    }
    if (StringUtil.isEmpty(enumStr)) {
      return value;
    }
    return value + "|" + enumStr;
  }

  private static String createEnumRemark(See see) {
    if (see == null) {
      return "";
    }
    Class clz = see.enums();
    if (clz == null || clz == Object.class) {
      return "";
    }
    if (!clz.isEnum()) {
      log.error("@see标签只能传枚举类");
      return "";
    }

    StringBuilder sb = new StringBuilder();
    // 通过枚举类获得所有枚举值
    try {
      Method method = clz.getMethod("values");
      Enum[] enums = (Enum[]) method.invoke(clz);
      for (Enum e : enums) {
        for (Field f : clz.getDeclaredFields()) {
          if (Modifier.isStatic(f.getModifiers())) {
            continue;
          }
          f.setAccessible(true);
          sb.append(f.get(e) + ":");
        }
        //执行过一遍长度是0，说明枚举没有字段
        if (sb.length() == 0) {
          log.error("@see标签里的枚举类需要自定义字段");
          return "";
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(",");
      }
      return sb.substring(0, sb.length() - 1);
    } catch (Exception ex) {
      log.warn("从enum提取备注出错：" + clz, ex);
      return "";
    }
  }

  private static String validateName(String message, Field field) {
    if (StringUtil.isEmpty(message)) {
      return field.getName();
    }
    return message;
  }

  public static String getRemark(Method method) {
    Remark remark =getAnnotation(method,Remark.class);
    if (remark != null) {
      return remark.value();

    }
    return getName(method);
  }

  public static String getName(Method method) {
    return method.getDeclaringClass().getSimpleName() + "." + method.getName();
  }

  public static String getUrl(Method m) {
    PostMapping po = getAnnotation(m,PostMapping.class);
    if (po != null && po.value().length > 0) {
      return po.value()[0];
    }
    RequestMapping rq =getAnnotation(m,RequestMapping.class);
    if (rq != null && rq.value().length > 0) {
      return rq.value()[0];
    }
    GetMapping getMapping =getAnnotation(m,GetMapping.class);
    if (getMapping != null && getMapping.value().length > 0) {
      return getMapping.value()[0];
    }
    return "";
  }

  public static String getUrl(Class clazz) {
    PostMapping po = getAnnotation(clazz,PostMapping.class);
    if (po != null && po.value().length > 0) {
      return po.value()[0];
    }
    RequestMapping rq =getAnnotation(clazz,RequestMapping.class);
    if (rq != null && rq.value().length > 0) {
      return rq.value()[0];
    }
    GetMapping getMapping =getAnnotation(clazz,GetMapping.class);
    if (getMapping != null && getMapping.value().length > 0) {
      return getMapping.value()[0];
    }
    return "";
  }

  public static <T extends Annotation> T getAnnotation(Class clazz, Class<T> annClass) {
    return (T) clazz.getAnnotation(annClass);
  }

  /**
   * 获取方法的注解，如果本身声明没有就去接口找
   */
  public static <T extends Annotation> T getAnnotation(Method method, Class<T> annClass) {
    T t = method.getAnnotation(annClass);
    if (t != null) {
      return t;
    }
    for (Class clz : method.getDeclaringClass().getInterfaces()) {
      try {
        for(Method ms:clz.getMethods()){
          //如果名称一样，参数个数一一样，就尝试获取
          if(ms.getName().equalsIgnoreCase(method.getName())&&ms.getParameterCount()==method.getParameterCount()){
            Method interfaceMethod = clz.getMethod(method.getName(), method.getParameterTypes());
            if (interfaceMethod != null) {
              return interfaceMethod.getAnnotation(annClass);
            }
          }
        }
      } catch (Exception ex) {
        log.warn("查找注解，忽略：" + ex.getMessage(),ex);
      }
    }
    return null;
  }
}

