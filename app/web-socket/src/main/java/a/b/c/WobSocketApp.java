package a.b.c;

import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class WobSocketApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(WobSocketApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("正常启动");
    }
}
