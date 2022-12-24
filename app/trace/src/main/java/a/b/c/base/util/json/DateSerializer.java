package a.b.c.base.util.json;

import a.b.c.base.util.DateTime;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Date;

public class DateSerializer extends JsonSerializer<Date> {
    public final static DateSerializer instance = new DateSerializer();

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(DateUtil.format(value, DateUtil.DateFormat.YEAR_TO_SECOND));
    }
}
