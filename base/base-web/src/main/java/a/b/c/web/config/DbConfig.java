package a.b.c.web.config;

import a.b.c.Constant;
import com.alibaba.druid.pool.DruidDataSource;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Configuration
@Slf4j
public class DbConfig {

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
