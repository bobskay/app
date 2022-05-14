package a.b.c.base.dao.sql;

import a.b.c.base.annotation.Column;
import a.b.c.base.annotation.Remark;
import a.b.c.base.utils.StringUtil;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Date;

public class SqlCreateUtil {

  public static final Integer INTEGER_LENGTH = 10;
  public static final String COLUMN_SEPARATORS = "_";

  /**
   * "alter table {db}.finance_pay_records modify column  `deposit_account` varchar(500) NOT NULL
   * DEFAULT '' COMMENT '四方返回-入款账号';";
   */
  public static String getModify(Field field) {
    String table = tableName(field.getDeclaringClass());
    String colName = toColumn(field.getName());
    StringBuilder sb = new StringBuilder();
    sb.append("alter table ").append(table);
    sb.append(" modify column ").append(colName);

    Column column = field.getAnnotation(Column.class);
    if (column == null) {
      return sb.toString() + "...";
    }
    String type = type(field, column);
    sb.append(type);
    String defaultValue = column.defaultValue();
    String extra = column.extra();
    String comment = getComment(field);

    if (column.unsigned()) {
      sb.append(" unsigned");
    }
    sb.append(" ").append("NOT NULL");

    if (StringUtil.isNotEmpty(defaultValue)) {
      if (isKeyword(defaultValue)) {
        sb.append(" ").append("DEFAULT " + defaultValue + " ");
      } else {
        sb.append(" ").append("DEFAULT '" + defaultValue + "' ");
      }
    } else if (StringUtil.isNotEmpty(extra)) {
      sb.append(" ").append(extra).append(" ");
    } else if (type.indexOf("varchar") != -1) {
      sb.append(" ").append("DEFAULT ''");
    }
    sb.append(" COMMENT ").append("'").append(comment).append("'");
    return sb.toString();
  }

  public static String type(Field field, Column column) {
    String type = column.type();
    if (StringUtil.isEmpty(type)) {
      type = toSqlType(field.getType());
    }
    Integer length = column.length();
    Integer fraction = column.fraction();
    if (length == null || length == 0) {
      return column.type();
    }
    if (fraction == null || fraction == 0) {
      return column.type() + "(" + length + ")";
    }
    return column.type() + "(" + length + "," + fraction + ")";
  }

  /**
   * 将java字段名转为数据库的列名
   */
  public static String toColumn(String str) {
    return StringUtil.camelConvert(str, COLUMN_SEPARATORS);
  }

  private static boolean isKeyword(String defaultValue) {
    return "CURRENT_TIMESTAMP".equalsIgnoreCase(defaultValue);
  }

  /**
   * 数据库类型对应的java类型
   */
  public static Class toJavaType(String dbType) {
    if (dbType.indexOf("varchar") != -1) {
      return String.class;
    }
    if (dbType.indexOf("tinyint") != -1) {
      return Integer.class;
    }
    if (dbType.indexOf("smallint") != -1) {
      return Integer.class;
    }
    if (dbType.indexOf("bigint") != -1) {
      return Long.class;
    }
    if (dbType.indexOf("int") != -1) {
      String length = StringUtil.pickAround(dbType, "(", ")");
      if (StringUtil.isEmpty(length) || Integer.parseInt(length) <= INTEGER_LENGTH) {
        return Integer.class;
      }
      return Long.class;
    }
    if (dbType.indexOf("timestamp") != -1||dbType.indexOf("date")!=-1) {
      return Date.class;
    }
    if (dbType.indexOf("decimal") != -1) {
      return BigDecimal.class;
    }
    if (dbType.indexOf("text") != -1) {
      return String.class;
    }

    if (dbType.indexOf("double") != -1) {
      return Double.class;
    }

    return Object.class;
  }

  /**
   * 获得表名
   */
  public static String tableName(Class clazz) {
    return toColumn(clazz.getSimpleName());
  }


  public static String getComment(Field field) {
    Remark remark = field.getAnnotation(Remark.class);
    if (remark != null) {
      return remark.value();
    }
    return "";
  }

  private static String toSqlType(Class type) {
    if (type == String.class) {
      return "varchar";
    }
    if (type == Integer.class || type == Long.class) {
      return "int";
    }
    if (type == Date.class) {
      return "timestamp";
    }
    if (type == BigDecimal.class) {
      return "decimal";
    }
    throw new RuntimeException("不支持的类型：" + type);
  }

}