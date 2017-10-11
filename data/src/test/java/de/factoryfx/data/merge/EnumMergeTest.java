package de.factoryfx.data.merge;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.EnumAttributeTest;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.util.Locale;

public class EnumMergeTest extends MergeHelperTestBase {
    public enum TestEnum{
        A,B;
    }

    public static class EnumMergeTestPojo extends Data {
        public final EnumAttribute<TestEnum> attribute=new EnumAttribute<>(TestEnum.class);
    }

    @Test
    public void test_merge_change(){
        EnumMergeTestPojo aTest1 = new EnumMergeTestPojo();
        aTest1.attribute.setEnum(TestEnum.A);

        EnumMergeTestPojo aTest2 = new EnumMergeTestPojo();
        aTest2.attribute.setEnum(TestEnum.B);

        Assert.assertEquals(TestEnum.A,aTest1.attribute.getEnum());
        Assert.assertTrue(merge(aTest1, aTest1, aTest2).hasNoConflicts());
        Assert.assertEquals(TestEnum.B,aTest1.attribute.getEnum());

        ObjectMapperBuilder.build().copy(aTest1);
        Assert.assertEquals(TestEnum.B,aTest1.attribute.getEnum());
    }



}