package a.b.c.trace.job;

import a.b.c.trace.component.socket.SocketClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.net.URISyntaxException;

@Component
public class SocketClientRestartJob {

    @Resource
    SocketClient socketClient;

    @Scheduled(cron = "0 0/10 * * * ? ")
    public void check() throws URISyntaxException, InterruptedException {
        socketClient.createClient();
    }
}
