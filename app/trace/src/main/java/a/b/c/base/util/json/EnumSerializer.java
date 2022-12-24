package a.b.c.base.util.json;

import a.b.c.base.enums.DbEnum;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class EnumSerializer extends JsonSerializer<DbEnum> {
    public final static EnumSerializer instance = new EnumSerializer();

    @Override
    public void serialize(DbEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNumber(value.getValue());
    }
}
