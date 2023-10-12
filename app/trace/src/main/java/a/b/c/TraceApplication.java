package a.b.c;

import a.b.c.exchange.Exchange;
import a.b.c.trace.cache.UserInfoCache;
import a.b.c.trace.component.socket.listener.AggTradeListener;
import a.b.c.trace.model.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@SpringBootApplication
@RestController
@Slf4j
@EnableScheduling
public class TraceApplication implements CommandLineRunner {

    @Resource
    AggTradeListener aggTradeListener;
    @Resource
    Exchange exchange;

    public static void main(String[] args) {
        SpringApplication.run(TraceApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("系统正常启动...1");
        aggTradeListener.mockPrice(exchange.getPrice(exchange.getSymbol()));
    }
}