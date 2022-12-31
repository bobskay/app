package a.b.c.base.service;

import a.b.c.base.dao.EntityInfo;
import a.b.c.base.dao.FieldInfo;
import a.b.c.base.dto.PageQueryDto;
import a.b.c.base.util.ClassUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.SneakyThrows;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class BaseService<T> {
    public static final String START = "Start";
    public static final String END = "End";
    public static final String LIST = "List";


    @SneakyThrows
    public QueryWrapper toWrapper(PageQueryDto query) {
        EntityInfo entityInfo = EntityInfo.getByQuery(query.getClass());
        QueryWrapper wrapper = new QueryWrapper<>();
        for (Field field : ClassUtil.getAllFields(query.getClass())) {
            if (Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            //分页信息不做查询条件
            if (field.getDeclaringClass() == PageQueryDto.class) {
                continue;
            }
            field.setAccessible(true);
            Object o = field.get(query);
            if (StringUtils.isEmpty(o)) {
                continue;
            }
            String fieldName = field.getName();
            if (field.getName().endsWith(START)) {
                String fName = fieldName.substring(0, fieldName.length() - START.length());
                FieldInfo fieldInfo = entityInfo.getField(fName);
                wrapper.ge(fieldInfo.getColumnName(), o);
                continue;
            }
            if (field.getName().endsWith(END)) {
                String fName = field.getName().substring(0, fieldName.length() - END.length());
                FieldInfo fieldInfo = entityInfo.getField(fName);
                if (o instanceof Date) {
                    o = new Date(((Date) o).getTime() + 999L);
                }
                wrapper.le(fieldInfo.getColumnName(), o);
                continue;
            }
            if (o instanceof Collection) {
                if(CollectionUtils.isEmpty((Collection) o)){
                    continue;
                }
                List list=new ArrayList<>();
                ((Collection<?>) o).forEach(data->{
                    if(data instanceof Enum){
                        list.add(data.toString());
                    }else{
                        list.add(data);
                    }
                });
                //如果查询条件是集合字段必须以list结尾
                FieldInfo fieldInfo = entityInfo.getField(fieldName.substring(0, fieldName.length() - LIST.length()));
                wrapper.in(fieldInfo.getColumnName(), list);
            } else {
                FieldInfo fieldInfo = entityInfo.getField(fieldName);
                wrapper.eq(fieldInfo.getColumnName(), o);
            }
        }
        return wrapper;
    }
}
