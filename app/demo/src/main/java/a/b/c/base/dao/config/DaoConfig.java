package a.b.c.base.dao.config;

import a.b.c.base.dao.id.IdGenerator;
import a.b.c.base.dao.id.IdProvider;
import a.b.c.base.dao.id.TableInfoMapper;
import a.b.c.base.utils.IdWorker;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoConfig {

  @Autowired
  ApplicationContext applicationContext;

  @Bean
  public IdWorker idWorker() {
    return new IdWorker(1);
  }

  @Bean
  public IdGenerator idGenerator(IdWorker idWorker) {
    IdProvider idProvider = count -> {
      Set<Long> ids = new HashSet<>();
      for (int i = 0; i < count; i++) {
        ids.add(idWorker.nextId());
      }
      return ids;
    };
    IdGenerator idGenerator = new IdGenerator(idProvider, 1000, tableName -> {
      //不能用直接注入的方式，不然启动的时候会死循环
      TableInfoMapper mapper = applicationContext.getBean(TableInfoMapper.class);
      return mapper.getColumns(tableName);
    });
    IdGenerator.INSTANCE = idGenerator;
    return idGenerator;
  }
}
