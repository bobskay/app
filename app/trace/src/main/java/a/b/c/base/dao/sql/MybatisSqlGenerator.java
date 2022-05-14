package a.b.c.base.dao.sql;


import a.b.c.Constant;
import a.b.c.base.annotation.Table;
import a.b.c.base.dao.Model;
import a.b.c.base.dao.Selector;
import a.b.c.base.enums.Dic;
import a.b.c.base.utils.ClassUtil;
import a.b.c.base.utils.DateTime;
import a.b.c.base.utils.StringUtil;
import cn.hutool.core.convert.Convert;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;

/**
 * sql生成器
 *
 * @author huawei
 */
@Slf4j
public class MybatisSqlGenerator {
  public static final String SITE_ID="stxx";

  /**
   * 以Start或者end结尾说明是查询开始和结束
   */
  public static final String QUERY_START = "Start";
  public static final String QUERY_END = "End";
  public static final String COLUMN_SEPARATORS = "_";

  /**
   * <p> 是否使用数据库自增的方式设置主键，如果未false就用id分配器
   * <p>系统启动的时候设置，程序里别改。
   * <p>默认是true，当标记了EnableFinanceSessionFactory就会改成配置的
   */
  public static boolean AUTO_INCREASE = false;

  /**
   * 如果数据库int类型长度小于等于这个值就转为int类型
   */
  public static final Integer INTEGER_LENGTH = 10;

  /**
   * 获取字段的默认值
   */
  public static Object getDefault(ColumnInfo col) {
    if("NULL".equalsIgnoreCase(col.getDefaulT())){
      col.setDefaulT(null);
    }
    String typeName = col.getType().toLowerCase();
    if (typeName.indexOf("varchar") != -1) {
      if(col.getDefaulT()==null){
        return "";
      }
      return col.getDefaulT();
    }
    if (StringUtil.isEmpty(col.getDefaulT())) {
      return null;
    }
    if (typeName.indexOf("tinyint") != -1) {
      return Integer.parseInt(col.getDefaulT());
    }
    if (typeName.indexOf("bigint") != -1) {
      return Long.parseLong(col.getDefaulT());
    }
    if (typeName.indexOf("int") != -1) {
      return Integer.parseInt(col.getDefaulT());
    }
    if (typeName.indexOf("timestamp") != -1||typeName.indexOf("date")!=-1||typeName.indexOf("datetime")!=-1) {
      if (col.getDefaulT().toUpperCase().contains("CURRENT_TIMESTAMP")) {
        return (Supplier) () -> new Date();
      }
      return new DateTime(col.getDefaulT()).toDate();
    }
    if (typeName.indexOf("decimal") != -1) {
      return new BigDecimal(col.getDefaulT());
    }
    if (typeName.indexOf("double") != -1) {
      return Convert.toDouble(col.getDefaulT());
    }
    throw new RuntimeException("未知的默认值：" + col.getType() + ":" + col.getDefaulT());
  }

  /**
   * 数据库转为java名称
   */
  public static String toJava(String columns) {
    StringBuilder sb = new StringBuilder();
    for (String col : columns.split(COLUMN_SEPARATORS)) {
      sb.append(StringUtil.firstUp(col));
    }
    return StringUtil.firstLower(sb.toString());
  }

  /**
   * 获得表名
   */
  public String tableName(Class clazz) {
    return toColumn(clazz.getSimpleName());
  }

  /**
   * 所有列
   */
  public String columns(Class clz) {
    StringBuilder columns = new StringBuilder();

    for (Field f :  ClassUtil.getAllFiled(clz)) {
      columns.append(" " + toColumn(f.getName()) + " as " + f.getName() + ",");
    }
    columns.deleteCharAt(columns.length() - 1);
    return columns.toString();
  }

  public static String getTableName(Class clz) {
    Table table = (Table) clz.getAnnotation(Table.class);
    if (table == null) {
      return Constant.TABLE_PIX+ toColumn(clz.getSimpleName());
    }
    return table.value();
  }
  /**
   * 生成insert
   */
  public String insert(Class clz) {
    StringBuilder insert = new StringBuilder("insert into ");
    insert.append(getTableName(clz));
    insert.append("(");
    for (Field f : clz.getDeclaredFields()) {
      //stxx特殊处理
      if(f.getName().equals(SITE_ID)){
        continue;
      }
      if (Modifier.isStatic(f.getModifiers())) {
        continue;
      }
      if ("id".equals(f.getName()) && AUTO_INCREASE) {
        continue;
      }
      insert.append(toColumn(f.getName())).append(",");
    }
    insert.deleteCharAt(insert.length()-1);
    insert.append(") values (");
    for (Field f : clz.getDeclaredFields()) {
      //stxx的值从financecontext
      if(f.getName().equals(SITE_ID)){
        continue;
      }
      if (Modifier.isStatic(f.getModifiers())) {
        continue;
      }
      if ("id".equals(f.getName()) && AUTO_INCREASE) {
        continue;
      }
      insert.append("#{" + f.getName() + "},");
    }
    insert.deleteCharAt(insert.length()-1);
    insert.append(")");
    return insert.toString();
  }


  /**
   * 将java字段名转为数据库的列名
   */
  public static String toColumn(String str) {
    return StringUtil.camelConvert(str, COLUMN_SEPARATORS);
  }

  /**
   * 生成非空字段的条件
   */
  public String condition(Model model) throws IllegalAccessException {
    return defaultQuery(model);
  }

  private String defaultQuery(Object query) throws IllegalAccessException {
    String and = " and ";
    StringBuilder condition = new StringBuilder();
    for (Field f : query.getClass().getDeclaredFields()) {
      f.setAccessible(true);
      Object value = f.get(query);
      String name = f.getName();
      if (value != null) {
        if (name.endsWith(QUERY_START)) {
          //用正则替换，只替换结尾的Start
          String column = toColumn(name.replaceAll(QUERY_START + "$", ""));
          condition.append(" and ").append(column);
          condition.append(">=#{").append(name).append("}");
        } else if (name.endsWith(QUERY_END)) {
          //用正则替换，只替换结尾的End
          String column = toColumn(name.replaceAll(QUERY_END + "$", ""));
          condition.append(" and ").append(column);
          condition.append("<=#{").append(name).append("}");
        } else {
          condition.append(" and ").append(toColumn(name));
          if (value instanceof Dic) {
            condition.append("=#{").append(name + ".key").append("}");
          } else {
            condition.append("=#{").append(name).append("}");
          }
        }
      }
    }
    if (condition.length() == 0) {
      throw new RuntimeException("查询条件不能为空");
    }
    return condition.substring(and.length());
  }


  /**
   * 生成非空字段的条件
   */
  public String condition(Selector selector) throws IllegalAccessException {
    return defaultQuery(selector);
  }

  /**
   * 获取selector对应的model
   */
  public static Class getModel(Selector selector) {
    ParameterizedType type = (ParameterizedType) selector.getClass().getGenericInterfaces()[0];
    return (Class) type.getActualTypeArguments()[0];

  }

  public String id(Model model) {
    return "id=" + model.getId();
  }

  public String updateNotNullById(Model model) throws IllegalAccessException {
    StringBuilder update=new StringBuilder(" set ");
    for (Field f : model.getClass().getDeclaredFields()) {
      String name = f.getName();
      if("id".equals(name)){
        continue;
      }
      f.setAccessible(true);
      Object value = f.get(model);

      if (value != null) {
        String column = toColumn(name.replaceAll(QUERY_END + "$", ""));
        update.append(column);
        update.append("=#{").append(name).append("}");
        update.append(",");
      }
    }
    if (update.length() == 0) {
      throw new RuntimeException("修改的字段不能为空");
    }
    update=update.deleteCharAt(update.length()-1);
    update.append(" where id=").append(model.getId()+"");
    return update.toString();
  }



}
