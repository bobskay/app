package a.b.c;

import a.b.c.base.utils.IdWorker;
import a.b.c.base.utils.LogUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class DemoApp implements CommandLineRunner {

  public static void main(String[] args) {
    SpringApplication.run(DemoApp.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    LogUtil.setRequestId(IdWorker.nextString());
    log.info("正常启动");
  }
}
