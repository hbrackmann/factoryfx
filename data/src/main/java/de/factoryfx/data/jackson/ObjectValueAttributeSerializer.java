package de.factoryfx.data.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;

/** ignore value, @Ignore annotation doesn't work therefore this custom Deserializer*/
public class ObjectValueAttributeSerializer extends JsonSerializer<ObjectValueAttribute> {
    @Override
    public void serialize(ObjectValueAttribute value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeNull();//nothing
//        gen.writeStartObject();
//        gen.writeEndObject();
        //nothing
    }
}
