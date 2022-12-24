package a.b.c.base.util.log;

import ch.qos.logback.classic.pattern.ClassicConverter;
import ch.qos.logback.classic.spi.ILoggingEvent;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class LogHostNameConfig extends ClassicConverter {
    private static String hostname;

    static {
        try {
            hostname = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            log.error("获取日志Ip异常", e);
            hostname = null;
        }
    }

    @Override
    public String convert(ILoggingEvent event) {
        return hostname;
    }
}