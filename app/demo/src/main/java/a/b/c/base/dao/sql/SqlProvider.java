package a.b.c.base.dao.sql;


import a.b.c.base.dao.Model;
import a.b.c.base.dao.Selector;
import a.b.c.base.dao.id.IdGenerator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

/**
 * @author huawei
 * @date 2020/05/27 16:11
 * @description 自动生成sql语句，为避免造成混乱，复杂的sql还是写在mapper.xml里面
 */
@Slf4j
public class SqlProvider {

  private static volatile Map<String, String> columnsMap = new ConcurrentHashMap<>();
  private static volatile Map<String, String> insertMap = new ConcurrentHashMap<>();

  MybatisSqlGenerator sqlGenerator = new MybatisSqlGenerator();


  public String selectByModel(Model model) throws Exception {
    Class clazz = model.getClass();
    StringBuilder select = new StringBuilder();
    String columns = getFormMap(columnsMap, clazz, sqlGenerator::columns);
    select.append("select ").append(columns);
    select.append(" from ").append(sqlGenerator.tableName(clazz));
    select.append(" where ").append(sqlGenerator.condition(model));
    return select.toString();
  }

  public String selectBySelector(Selector selector) throws Exception {
    Class clazz = MybatisSqlGenerator.getModel(selector);
    StringBuilder select = new StringBuilder();
    String columns = getFormMap(columnsMap, clazz, sqlGenerator::columns);
    select.append("select ").append(columns);
    select.append(" from ").append(sqlGenerator.tableName(clazz));
    select.append(" where ").append(sqlGenerator.condition(selector));
    return select.toString();
  }

  public String insert(Model model) {
    try {
      if (!MybatisSqlGenerator.AUTO_INCREASE && (model.getId() == null || model.getId()==0)) {
        model.setId(IdGenerator.nextId());
      }
      IdGenerator.nullToDefault(model);
      String sql= getFormMap(insertMap, model.getClass(), (clazz)->{
        String genSql=sqlGenerator.insert(clazz);
        log.info(clazz.getSimpleName()+"初始化sql语句:"+":"+genSql);
        return genSql;
      });

      return sql;
    } catch (Exception ex) {
      log.error("生成insert语句出错：" + model, ex);
      throw ex;
    }
  }

  public String update(Model model) throws Exception {
    try {
      StringBuilder update = new StringBuilder();
      update.append("update ").append(sqlGenerator.tableName(model.getClass()));
      update.append(sqlGenerator.updateNotNullById(model));
      return update.toString();
    } catch (Exception ex) {
      log.error("生成update语句出错：" + model, ex);
      throw ex;
    }
  }

  /**
   * 从map里获得sql，如果没有就新增
   *
   * @param map     缓存
   * @param creator 生成sql的方法
   * @Param clazz model类
   */
  public static String getFormMap(Map<String, String> map, Class clazz,
      Function<Class, String> creator) {
    String className = clazz.getName();
    String sql = map.get(className);
    if (sql != null) {
      return sql;
    }
    synchronized (map) {
      sql = map.get(className);
      if (sql != null) {
        return sql;
      }
      sql = creator.apply(clazz);
      map.put(className, sql);
      return sql;
    }
  }

}
