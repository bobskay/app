package a.b.c.base.util;

import a.b.c.exchange.response.ApiResponse;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ClassUtil {
    public static List<Field> getAllFields(Class clazz) {
        List<Field> list = new ArrayList();
        addAllFields(clazz, list);
        return list;
    }

    private static void addAllFields(Class clazz, List<Field> list) {
        if (clazz == Object.class) {
            return;
        }
        for (Field f : clazz.getDeclaredFields()) {
            list.add(f);
        }
        addAllFields(clazz.getSuperclass(), list);
    }

    @SneakyThrows
    public static <T> T newInstance(Class clazz){
        return (T)clazz.newInstance();
    }
}
