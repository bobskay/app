package a.b.c.base.utils;

import a.b.c.Constant;
import a.b.c.base.enums.Regex;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.Jdk8DateCodec;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.alibaba.fastjson.util.IdentityHashMap;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;


/**
 * json操作工具类，加载后会修改fastJson的默认规则
 *
 * @author huawei
 */
@Slf4j
public class JsonUtil {

  public static JsonUtil DEFAULT = new JsonUtil();

  static {
    //序列化
    Map<Class, ObjectSerializer> serializeConfig = new HashMap(Constant.DEFAULT_CAPACITY);
    serializeConfig.put(BigInteger.class, ToStringSerializer.instance);
    serializeConfig.put(Long.class, ToStringSerializer.instance);
    serializeConfig.put(Long.TYPE, ToStringSerializer.instance);
    serializeConfig.put(LocalDateTime.class, localDateTimeSerialize());

    //反序列化
    Map<Class, ObjectDeserializer> parseConfig = new HashMap<>(Constant.DEFAULT_CAPACITY);
    parseConfig.put(LocalDateTime.class, new LocalDateTimeDeserializer());
    parseConfig.put(LocalDate.class, new LocalDateTimeDeserializer());
    updatGlobalConfig(serializeConfig, parseConfig);
  }


  //localDateTime返回值
  private static ObjectSerializer localDateTimeSerialize() {
    return (serializer, object, fieldName, fieldType, features) -> {
      SerializeWriter out = serializer.out;
      if (object == null) {
        out.writeNull();
      } else {
        String strVal= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format((LocalDateTime) object);
        out.writeString(strVal);
      }
    };
  }

  /**
   * 将对象转为字符串
   */
  public String toString(Object obj) {
    return JSON.toJSONString(obj);
  }

  /**
   * 将字符串转为java对象
   */
  public <T> T toBean(String str, Class<T> t) {
    return JSON.parseObject(str, t);
  }

  public <T> T toBean(String str, TypeReference<T> tTypeReference) {
    return JSON.parseObject(str, tTypeReference);
  }

  public String toFormatString(Object obj) {
    return JSON.toJSONString(obj, SerializerFeature.PrettyFormat);
  }


  /**
   * 设置全局的js配置
   *
   * @param serializeConfig 序列化配置，不允许为空，如果确认不需要配置传个空map进来
   * @param parseConfig     反序列化配置，不允许为空，如果确认不需要配置传个空map进来
   * @param features        其它配置
   */
  public static void updatGlobalConfig(Map<Class, ObjectSerializer> serializeConfig,
      Map<Class, ObjectDeserializer> parseConfig,
      SerializerFeature... features) {
    for (Map.Entry<Class, ObjectSerializer> en : serializeConfig.entrySet()) {
      SerializeConfig.globalInstance.put(en.getKey(), en.getValue());
    }
    for (Map.Entry<Class, ObjectDeserializer> en : parseConfig.entrySet()) {
      ParserConfig.getGlobalInstance().putDeserializer(en.getKey(), en.getValue());
    }

    JSON.DEFAULT_GENERATE_FEATURE = JSON.DEFAULT_GENERATE_FEATURE| SerializerFeature.WriteMapNullValue.mask;
    for (SerializerFeature f : features) {
      JSON.DEFAULT_GENERATE_FEATURE = JSON.DEFAULT_GENERATE_FEATURE | f.mask;
    }

    log.debug(
        "init jsonUtil:serializeConfig=" + serializeConfig.size() + ",parseConfig=" + parseConfig
            .size());
    debugConfig(SerializeConfig.globalInstance);
  }

  private static void debugConfig(SerializeConfig globalInstance) {
    if (!log.isTraceEnabled()) {
      return;
    }
    try {
      Field field = SerializeConfig.class.getDeclaredField("serializers");
      field.setAccessible(true);

      IdentityHashMap<Type, ObjectSerializer> map = (IdentityHashMap<Type, ObjectSerializer>) field
          .get(globalInstance);
      Field buckets = IdentityHashMap.class.getDeclaredField("buckets");
      buckets.setAccessible(true);
      Object[] entry = (Object[]) buckets.get(map);

      for (Object o : Arrays.asList(entry)) {
        if (o != null) {
          Field k = o.getClass().getDeclaredField("key");
          Field v = o.getClass().getDeclaredField("value");
          k.setAccessible(true);
          v.setAccessible(true);
          log.trace(k.get(o) + "=" + v.get(o));
        }
      }
    } catch (Exception ex) {
      log.info("print error:" + ex.getMessage(), ex);
    }
  }


  /**
   * 自定义LocalDateTime转换规则额，
   */
  public static class LocalDateTimeDeserializer extends Jdk8DateCodec {

    @Override
    protected LocalDateTime parseDateTime(String text, DateTimeFormatter formatter) {
      if (StringUtil.isEmpty(text)) {
        return null;
      }
      //如果是纯数字，就认为是ms，转为long
      if (text.matches(Regex.REG_INTEGER)) {
        Long time = Long.parseLong(text);
        return Instant.ofEpochMilli(time).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
      }
      try {
        return super.parseDateTime(text, formatter);
      } catch (Exception ex) {
        log.error("日期格式不正确：{}", text, ex);
        return null;
      }
    }

    @Override
    protected LocalDate parseLocalDate(String text, String format, DateTimeFormatter formatter) {
      try {
        //日期字段存了00:00:00,替换调
        String time = " 00:00:00";
        if (text.endsWith(time)) {
          text = text.replace(time, "");
        }
        return super.parseLocalDate(text, format, formatter);
      } catch (Exception ex) {
        log.error("将{}转为LocalDate出错：{}", text, ex.getMessage(), ex);
        return null;
      }
    }
  }

  public static String toJs(Object o) {
    return DEFAULT.toString(o);
  }

  public static <T> T toClass(String str, Class<T> t) {
    return DEFAULT.toBean(str, t);
  }

  public static <T> T toClass(String str, TypeReference<T> tTypeReference) {
    return DEFAULT.toBean(str, tTypeReference);
  }
}
