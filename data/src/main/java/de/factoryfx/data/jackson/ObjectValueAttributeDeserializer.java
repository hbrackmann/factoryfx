package de.factoryfx.data.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;

/** ignore value, @Ignore annotation doesn't work therefore this custom Deserializer*/
public class ObjectValueAttributeDeserializer extends JsonDeserializer {
    @Override
    public ObjectValueAttribute deserialize(JsonParser jp, DeserializationContext ctxt) {
        return new ObjectValueAttribute();
    }
}
