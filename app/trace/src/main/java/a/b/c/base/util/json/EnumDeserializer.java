package a.b.c.base.util.json;

import a.b.c.base.enums.DbEnum;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

@Slf4j
public class EnumDeserializer extends JsonDeserializer {
    public final static EnumDeserializer instance = new EnumDeserializer();

    @SneakyThrows
    public DbEnum deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        String currentName = jsonParser.currentName();
        Object currentValue = jsonParser.getCurrentValue();
        Class findPropertyType = BeanUtils.findPropertyType(currentName, currentValue.getClass());
        DbEnum[] obs = (DbEnum[]) findPropertyType.getMethod("values").invoke(findPropertyType);
        for (DbEnum o : obs) {
            if (o.getValue().toString().equals(jsonParser.getText())) {
                return o;
            }
        }
        log.warn("无法将{}转为枚举：{}", jsonParser.getText(), findPropertyType.getName());
        return null;
    }
}
