package a.b.c.base.util;

import a.b.c.exchange.dto.OpenOrder;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CollectionUtil {

    /**
     * 提取某个字段
     */
    public static <K, D> List<K> getField(List<D> list, Function<D, K> getMethod) {
        return list.stream().map(getMethod).collect(Collectors.toList());
    }

    public static <K, D> Map<K, D> toMap(List<D> list, Function<D, K> getKey) {
        Map map = new LinkedHashMap();
        for (D d : list) {
            K k = getKey.apply(d);
            map.put(k, d);
        }
        return map;
    }

    public static boolean isEmpty(List<OpenOrder> openOrders) {
        return openOrders == null || openOrders.isEmpty();
    }
}
