package a.b.c.base.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CollectionUtils {

  /**
   * 将collection转为map
   */
  public static <K, V> Map<K, V> toMap(Collection<V> data, Function<V, K> getKey) {
    Map<K, V> map = new HashMap<>();
    for (V t : data) {
      K key = getKey.apply(t);
      map.put(key, t);
    }
    return map;
  }

  /**
   * 将collection按key分组
   */
  public static <K, V> Map<K, List<V>> groupBy(Collection<V> data, Function<V, K> getKey) {
    Map<K, List<V>> map = new HashMap<>();
    for (V t : data) {
      K key = getKey.apply(t);
      List<V> list=map.get(key);
      if(list==null){
        list=new ArrayList<>();
      }
      list.add(t);
      map.put(key, list);
    }
    return map;
  }

  /**
   * 提前集合里的某个字段
   * */
  public static <K, V> List<K> getFieldValues(Collection<V> data, Function<V, K> getValue) {
    List list = new ArrayList();
    for (V t : data) {
      K value = getValue.apply(t);
      list.add(value);
    }
    return list;
  }

  /**
   * 将list按个数拆开
   * */
  public static List<List> split(List all, int size) {
    List list = new ArrayList();
    List temp = new ArrayList();
    for (Object o : all) {
      temp.add(o);
      if (temp.size() >= size) {
        list.add(temp);
        temp = new ArrayList();
      }
    }
    if (temp.size() > 0) {
      list.add(temp);
    }
    return list;
  }
}
