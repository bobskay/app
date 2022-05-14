package a.b.c.base.dao.id;

import a.b.c.base.dao.sql.ColumnInfo;
import a.b.c.base.dao.Model;
import a.b.c.base.dao.sql.MybatisSqlGenerator;
import a.b.c.base.dao.sql.SqlCreateUtil;
import a.b.c.base.monitor.MonitorLogApi;
import a.b.c.base.utils.ClassUtil;
import a.b.c.base.utils.CollectionUtils;
import a.b.c.base.utils.MapUtil;
import a.b.c.base.utils.StringUtil;
import cn.hutool.core.convert.Convert;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@EnableScheduling
public class IdGenerator implements CommandLineRunner {

  public static IdGenerator INSTANCE;

  private IdProvider idProvider;
  private final int cacheSize;
  private Queue<Long> idCache;
  private Function<String, List<ColumnInfo>> getTableColumnInfo;

  /**
   * insert的时候有些值是null，需要用数据库的默认值
   */
  private ConcurrentHashMap<String, Map<String, Object>> defaultValue = new ConcurrentHashMap();


  /**
   * @param idProvider         表已经被获取的最大id
   * @param cacheSize          每次获取id个数
   * @param getTableColumnInfo 获取表的列
   */
  public IdGenerator(IdProvider idProvider,  int cacheSize,
      Function<String, List<ColumnInfo>> getTableColumnInfo) {
    this.idProvider = idProvider;
    this.cacheSize = cacheSize;
    this.getTableColumnInfo = getTableColumnInfo;
    LinkedBlockingQueue list=new LinkedBlockingQueue(cacheSize);
    this.idCache = list;
  }

  /**
   * 获取注解
   */
  public static Long nextId() {
    if (INSTANCE == null) {
      throw new RuntimeException("IdGenerator未初始化");
    }
    return INSTANCE.next();
  }

  /**
   * 将mode对象里的null字段用数据库默认值替换
   */
  public static void nullToDefault(Model model) {
    if (INSTANCE == null) {
      log.warn("IdGenerator未初始化无法设置默认值");
      return;
    }
    INSTANCE.nullToDefaultInner(model);
  }

  /**
   * 将null转换成数据库的默认值
   */
  public void nullToDefaultInner(Model model) {
    String tableName = MybatisSqlGenerator.getTableName(model.getClass());

    Map<String, Object> defaultMap = MapUtil.get(defaultValue, tableName, () -> {
      log.info("准备初始化表的默认值：" + tableName);
      Map<String, Object> map = new HashMap();
      List<ColumnInfo> cols = getTableColumnInfo.apply(tableName);

      //检查字段和model是否匹配
      try {
        checkFields(model, cols);
      } catch (Exception ex) {
        log.error("检查字段出错："+model.getClass()+ ex.getMessage(), ex);
      }

      for (ColumnInfo col : cols) {
        try {
          Object value = MybatisSqlGenerator.getDefault(col);
          if (!StringUtil.isEmpty(value)) {
            log.info(col.getField() + ":" + col.getComment() + "默认值：" + value);
          }
          map.put(MybatisSqlGenerator.toJava(col.getField()), value);
        } catch (Exception ex) {
          log.error("获取默认值出错,数据库查到的字段信息："+col+",异常内容：" + ex.getMessage(), ex);
        }
      }
      log.info("初始化表的默认值：" + tableName + ":" + map);
      return map;
    });
    for (Field f : model.getClass().getDeclaredFields()) {
      if (Modifier.isStatic(f.getModifiers())) {
        continue;
      }
      f.setAccessible(true);
      try {
        Object value = f.get(model);
        if (value == null) {
          Object def = defaultMap.get(f.getName());
          if (def instanceof Supplier) {
            def = ((Supplier) def).get();
          }
          value = Convert.convert(f.getType(), def);
          f.set(model, value);
        }
      } catch (Exception ex) {
        log.error("设置字段默认值出错：" + model.getClass().getName() + "." + f.getName(), ex);
      }
    }
  }

  private void checkFields(Model model, List<ColumnInfo> cols) {
    List<Field> fields = ClassUtil.getAllFiled(model.getClass());
    Map<String, Field> allField = CollectionUtils.toMap(fields, Field::getName);
    for (ColumnInfo col : cols) {
      String name = MybatisSqlGenerator.toJava(col.getField());
      Field field = allField.get(name);
      //数据所有表加了updatedAt，代码里没用
      if (field == null ) {
        if(!"updatedAt".equals(name)&& !"stxx".equals(name)){
          log.error(model.getClass().getSimpleName() + "缺少字段：" + name);
        }
        continue;
      }
      allField.remove(name);
      String fName = model.getClass().getName() + "." + field.getName();
      // @Column注解中的type属性
      Class javaType = SqlCreateUtil.toJavaType(col.getType());
      if (field.getType() != javaType) {
        if(field.getType()==Integer.class&&javaType==Long.class){
          continue;
        }
        if(field.getType()==Long.class&&javaType==Integer.class){
          continue;
        }
        if(field.getType()== LocalDateTime.class && javaType== Date.class){
          continue;
        }
        if(field.getType()== Date.class && javaType== LocalDateTime.class){
          continue;
        }
        if(field.getType()== Integer.class && javaType==Boolean.class){
          continue;
        }
        if(field.getType()==Boolean.class && javaType==Integer.class) {
        	continue;
        }
        log.error(fName + "的类型和数据库的不符:" + col.getType());
      }
    }
    for (Field f : allField.values()) {
      String table = SqlCreateUtil.tableName(model.getClass());
      log.error(table + "缺少column:" + SqlCreateUtil.getModify(f));
    }
  }

  public Long next() {
    while (true) {
      Long id = idCache.poll();
      if (id != null) {
        return id;
      }
      fillId();
    }
  }

  private void fillId() {
    long start = System.currentTimeMillis();
    int size = cacheSize - idCache.size();
    if (size <= 0) {
      return;
    }
    if(size<cacheSize/2){
      log.debug("id剩余较多，跳过："+idCache.size());
      return;
    }

    log.info("当前id剩余{}，补充{}个，到{}：", idCache.size(), size, cacheSize);
    Set<Long> setId =idProvider.nexId(size);

    List<Long> ids=new ArrayList(setId);
    Collections.sort(ids);

    for (Long id : ids) {
      if (!idCache.offer(id)) {
        break;
      }
    }
    long time = System.currentTimeMillis() - start;
    String msg = MonitorLogApi.toTimeString("补充id耗时", time);
    log.info(msg);
  }


  @Scheduled(cron = "0 * * * * ?")
  public void addId() {
    fillId();
  }

  @Override
  public void run(String... args) throws Exception {
    new Thread(() -> {
      log.info("初始化id");
      addId();
    }).start();

  }
}
