package a.b.c.base.config;

import a.b.c.base.util.json.EnumDeserializer;
import a.b.c.base.util.json.EnumUtil;
import a.b.c.base.util.json.JsonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Bean
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        SimpleModule module = new SimpleModule();
        //和数据库对应的枚举
//        for (Class clazz : EnumUtil.allDbEnum()) {
//            module.addDeserializer(clazz, EnumDeserializer.instance);
//        }
        JsonUtil.registerModule(module);
        return JsonUtil.DEFAULT_MAPPER;
    }


}