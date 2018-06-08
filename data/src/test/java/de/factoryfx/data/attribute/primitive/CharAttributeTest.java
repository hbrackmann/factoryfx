package de.factoryfx.data.attribute.primitive;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class CharAttributeTest {

    @Test
    public void test_json(){
        CharAttribute attribute = new CharAttribute();
        char value = 'a';
        attribute.set(value);
        CharAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assert.assertEquals(value,copy.get().charValue());
    }

}