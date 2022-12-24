package a.b.c.base.dao;

import a.b.c.base.Message;
import a.b.c.base.dto.PageQueryDto;
import a.b.c.base.util.ClassUtil;
import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityInfo {
    private static ConcurrentHashMap<String, EntityInfo> ENTITY_MAP = new ConcurrentHashMap<>();


    private Map<String, FieldInfo> fieldsMap = new ConcurrentHashMap<>();
    private Class entityClass;

    public FieldInfo getField(String fieldName) {
        FieldInfo fieldInfo = fieldsMap.get(fieldName);
        if (fieldInfo == null) {
            throw new Message("找不到字段：" + entityClass.getName() + "." + fieldName);
        }
        return fieldInfo;
    }


    /**
     * 传入子类，通过范型获取实际的实体类
     */
    public static EntityInfo getByQuery(Class<? extends PageQueryDto> clazz) {
        ParameterizedType type = (ParameterizedType) clazz.getGenericSuperclass();
        EntityInfo entityInfo = EntityInfo.get((Class) type.getActualTypeArguments()[0]);
        return entityInfo;
    }

    public static EntityInfo get(Class clazz) {
        EntityInfo entityInfo = ENTITY_MAP.get(clazz.getName());
        if (entityInfo != null) {
            return entityInfo;
        }

        synchronized (EntityInfo.class) {
            entityInfo = ENTITY_MAP.get(clazz.getName());
            if (entityInfo != null) {
                return entityInfo;
            }

            entityInfo = new EntityInfo();
            for (Field f : ClassUtil.getAllFields(clazz)) {
                if (Modifier.isStatic(f.getModifiers())) {
                    continue;
                }
                FieldInfo fieldInfo = new FieldInfo();
                fieldInfo.setFieldName(f.getName());
                fieldInfo.setColumnName(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, f.getName()));
                entityInfo.fieldsMap.put(fieldInfo.getFieldName(), fieldInfo);
            }
            entityInfo.entityClass = clazz;
            ENTITY_MAP.put(clazz.getName(), entityInfo);
            return entityInfo;
        }
    }
}
