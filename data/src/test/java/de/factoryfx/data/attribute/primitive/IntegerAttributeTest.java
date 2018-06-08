package de.factoryfx.data.attribute.primitive;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntegerAttributeTest {

    @Test
    public void test_json(){
        IntegerAttribute attribute = new IntegerAttribute();
        int value = 1;
        attribute.set(value);
        IntegerAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get().intValue());
    }

}