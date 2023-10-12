package a.b.c.trace.config;

import a.b.c.Constant;
import a.b.c.trace.component.socket.SocketClient;
import a.b.c.exchange.Exchange;
import a.b.c.exchange.socket.listener.AccountListener;
import a.b.c.exchange.socket.listener.MessageListener;
import a.b.c.trace.enums.Currency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collection;

@Configuration
@Slf4j
public class ExchangeConfig {

    @Resource
    ApplicationContext applicationContext;

    @Bean
    public SocketClient socketClient(Exchange exchange) throws Exception {
        Collection<MessageListener> msgListener = applicationContext.getBeansOfType(MessageListener.class).values();
        Collection<AccountListener> acListeners = applicationContext.getBeansOfType(AccountListener.class).values();
        return new SocketClient(exchange, new ArrayList<>(msgListener), new ArrayList<>(acListeners));
    }

    @Bean
    public Exchange exchange(){
        return Exchange.getInstance(Constant.SYMBOL, 2);
    }
}
