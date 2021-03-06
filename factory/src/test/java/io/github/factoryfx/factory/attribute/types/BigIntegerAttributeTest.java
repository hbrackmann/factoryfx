package io.github.factoryfx.factory.attribute.types;

import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.math.BigInteger;

public class BigIntegerAttributeTest {
    @Test
    public void test_json(){
        BigIntegerAttribute attribute = new BigIntegerAttribute();
        BigInteger value = BigInteger.valueOf(1);
        attribute.set(value);
        BigIntegerAttribute copy = ObjectMapperBuilder.build().copy(attribute);
        Assertions.assertEquals(value,copy.get());
    }
}