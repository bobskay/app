package a.b.c.base.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import lombok.SneakyThrows;

/**
 * 操作class的工具类
 *
 * @author huawei
 */
public class ClassUtil {

  @SneakyThrows
  public static <T> T newInstance(Class<T> clazz) {
    return clazz.newInstance();
  }

  private static ConcurrentHashMap<String,List<Field>> fieldMap=new ConcurrentHashMap<>();


  public static boolean isPrimitiveType(String name) {
    if (name.indexOf(".") == -1) {
      return true;
    }
    if (name.startsWith("java.lang")) {
      return true;
    }
    if (name.startsWith("java.math")) {
      return true;
    }
    if (name.equals(Date.class.getName())) {
      return true;
    }
    if(name.startsWith("java.time")){
      return true;
    }
    return false;
  }

  public static boolean isPrimitiveType(Class clazz) {
    return isPrimitiveType(clazz.getName());
  }

  public static List<Field> getAllFiled(Class clazz) {
    return MapUtil.get(fieldMap,clazz.getName(),()->{
      List list = new ArrayList();
      addField(list, clazz);
      return list;
    });
  }

  public static void addField(List<Field> list, Class clazz) {
    if (clazz == null) {
      return;
    }
    if (clazz == Object.class) {
      return;
    }
    if (ClassUtil.isPrimitiveType(clazz)) {
      return;
    }
    for (Field f : clazz.getDeclaredFields()) {
      if (Modifier.isStatic(f.getModifiers())) {
        continue;
      }
      f.setAccessible(true);
      list.add(f);
    }
    addField(list, clazz.getSuperclass());
  }

  public static  <T> T getAnnotation(Class clazz, Class<T> annClass) {
    if (clazz == null) {
      return null;
    }
    T ann = (T) clazz.getAnnotation(annClass);
    if (ann != null) {
      return ann;
    }
    return getAnnotation(clazz.getSuperclass(), annClass);
  }

  /** 通过注解获取方法 */
  public static List<Method> getMethodsByAnnotation(Class clazz,Class annotationClass){
    List list=new ArrayList();
    addMethod(list,clazz,annotationClass);
    return list;
  }

  private static void addMethod(List methods,Class clazz,Class annotationClass){
    for(Method method:clazz.getDeclaredMethods()){
      Annotation an=method.getAnnotation(annotationClass);
      if(an!=null){
        methods.add(method);
      }
    }
    Class parent=clazz.getSuperclass();
    if(parent!=Object.class&&parent!=Class.class){
      addMethod(methods,parent,annotationClass);
    }
  }

}
