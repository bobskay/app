import a.b.c.base.enums.DbEnum;
import com.google.common.base.CaseFormat;

import java.lang.reflect.Field;
import java.util.Date;

public class CreateTable {


    private static String createSql(Class clazz) {
        StringBuilder sql = new StringBuilder("create table " + columnName(clazz.getSimpleName()) + "(\n");
        for (Field field : clazz.getDeclaredFields()) {
            sql.append("    ").append(newField(field)).append(",\n");
        }
        sql.append("    PRIMARY KEY (`id`)\n");
        sql.append(") ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        return sql.toString();
    }


    private static String newField(Field field) {
        String name = columnName(field.getName());
        String type = getColumnType(field.getType());
        StringBuilder sb = new StringBuilder();
        sb.append(name).append(" ");
        sb.append(" ").append(type);
        sb.append(" default " + defaultValue(field.getType()));
        sb.append(" comment ''");
        return sb.toString();
    }

    private static String getColumnType(Class clazz) {
        if (clazz == Long.class || clazz == Integer.class) {
            return "bigint";
        }
        if (DbEnum.class.isAssignableFrom(clazz)) {
            return "tinyint";
        }
        if (Date.class.isAssignableFrom(clazz)) {
            return "datetime";
        }
        if (clazz == String.class) {
            return "varchar(255)";
        }
        throw new RuntimeException("未知到类型" + clazz);
    }

    private static String defaultValue(Class clazz) {
        if (clazz == Long.class || clazz == Integer.class) {
            return "0";
        }
        if (DbEnum.class.isAssignableFrom(clazz)) {
            return "0";
        }
        if (Date.class.isAssignableFrom(clazz)) {
            return "null";
        }
        if (clazz == String.class) {
            return "''";
        }
        throw new RuntimeException("未知到类型" + clazz);
    }

    private static String columnName(String fieldName){
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE,fieldName);
    }

}
