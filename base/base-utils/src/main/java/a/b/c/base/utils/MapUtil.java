package a.b.c.base.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;


/**
 *
 * @author huawei
 */
@Slf4j
public class MapUtil {


  public static <T> T get(ConcurrentHashMap<String, T> map, String key, Supplier<T> supplier) {
    T t = map.get(key);
    if (t != null) {
      return t;
    }
    synchronized (map) {
      t = map.get(key);
      if (t != null) {
        return t;
      }
      T value=supplier.get();
      if(value==null){
        log.error("初始化缓存失败："+key,new Exception());
        return null;
      }
      map.put(key, value);
      return value;
    }
  }
}
