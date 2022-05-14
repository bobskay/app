package a.b.c.demo.config;

import java.io.IOException;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@Slf4j
public class DaoConfiguration {

  @Bean
  public SqlSessionFactoryBean sqlSessionFactory(DataSource dataSource) {
    SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
    sqlSessionFactoryBean.setDataSource(dataSource);
    try {
      sqlSessionFactoryBean.setMapperLocations((new PathMatchingResourcePatternResolver())
          .getResources("classpath:/mapper/**/*Mapper.xml"));
    } catch (IOException ex) {
      log.error("读取mapper文件出错", ex);
    }
    return sqlSessionFactoryBean;
  }
}
