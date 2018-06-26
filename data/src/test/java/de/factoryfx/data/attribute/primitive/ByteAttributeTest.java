package de.factoryfx.data.attribute.primitive;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

public class ByteAttributeTest {

    @Test
    public void test_json(){
        ByteAttribute attribute = new ByteAttribute();
        byte value = 10;
        attribute.set(value);
        ByteAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get().byteValue());
    }


}