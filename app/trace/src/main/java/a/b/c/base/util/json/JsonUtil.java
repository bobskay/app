package a.b.c.base.util.json;

import a.b.c.base.enums.DbEnum;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class JsonUtil {
    /**
     * 默认的序列化
     * */
    public final static ObjectMapper DEFAULT_MAPPER = defaultMapper(new ObjectMapper());
    /**
     * 用于显示用的mapper，忽略null
     * */
    public final static ObjectMapper PRETTY_MAPPER = prettyMapper(new ObjectMapper());


    private static ObjectMapper defaultMapper(ObjectMapper mapper) {
        //反序列化时忽略json中存在但Java对象不存在的属性
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        //序列化时日期格式默认为yyyy-MM-dd'T'HH:mm:ss.SSSZ
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Date.class, DateSerializer.instance);
        module.addDeserializer(Date.class, DateDeserializer.instance);

        //long转为string
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);

        //数据库枚举
        module.addSerializer(DbEnum.class, EnumSerializer.instance);

        mapper.registerModule(module);
        return mapper;
    }

    private static ObjectMapper prettyMapper(ObjectMapper objectMapper) {
        ObjectMapper mapper= defaultMapper(objectMapper);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        SimpleModule module = new SimpleModule();
        module.addSerializer(Date.class, new JsonSerializer<Date>(){
            @Override
            public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString(DateUtil.format(value));
            }
        });
        mapper.registerModule(module);
        return mapper;
    }

    @SneakyThrows
    public static String toJs(Object obj) {
        return DEFAULT_MAPPER.writeValueAsString(obj);
    }

    @SneakyThrows
    public static <T> T toBean(String js, Class<T> clazz) {
        return DEFAULT_MAPPER.readValue(js, clazz);
    }

    public static void registerModule(SimpleModule module) {
        DEFAULT_MAPPER.registerModule(module);
        PRETTY_MAPPER.registerModule(module);
    }

    @SneakyThrows
    public static String prettyJs(Object obj){
        return PRETTY_MAPPER.writeValueAsString(obj);
    }
}
