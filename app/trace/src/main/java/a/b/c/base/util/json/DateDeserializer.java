package a.b.c.base.util.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class DateDeserializer extends JsonDeserializer<Date> {
    public final static DateDeserializer instance = new DateDeserializer();

    @SneakyThrows
    public Date deserialize(JsonParser jsonParser, DeserializationContext ctxt) {
        String str = jsonParser.getText();
        return DateUtil.toDate(str);
    }
}
