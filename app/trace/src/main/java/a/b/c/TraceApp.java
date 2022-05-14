package a.b.c;

import a.b.c.base.utils.IdWorker;
import a.b.c.base.utils.LogUtil;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class TraceApp implements CommandLineRunner {

  public static void main(String[] args) {
    args=new String[]{"--spring.config.additional-location=/opt/config/db.properties"};
    SpringApplication.run(TraceApp.class, args);
  }

  @Override
  public void run(String... args) {
    LogUtil.setRequestId(IdWorker.nextString());
    log.info("正常启动");
  }
}
