package de.factoryfx.model.merge;

import de.factoryfx.model.testfactories.ExampleFactoryA;
import de.factoryfx.model.testfactories.ExampleFactoryB;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceMergeTest extends MergeHelperTestBase{


    @Test
    public void test_same(){
        ExampleFactoryA current = new ExampleFactoryA();
        ExampleFactoryB newValue = new ExampleFactoryB();
        current.referenceAttribute.set(newValue);

        ExampleFactoryA newVersion = new ExampleFactoryA();
        newVersion.referenceAttribute.set(newValue);

        Assert.assertTrue( this.merge(current, current, newVersion).hasNoConflicts());
        Assert.assertEquals(newValue, current.referenceAttribute.get());
    }

    @Test
    public void test_merge_change(){
        ExampleFactoryA current = new ExampleFactoryA();
        ExampleFactoryB newValue = new ExampleFactoryB();
        current.referenceAttribute.set(newValue);

        ExampleFactoryA newVersion = new ExampleFactoryA();
        ExampleFactoryB newValue2 = new ExampleFactoryB();
        newVersion.referenceAttribute.set(newValue2);

        Assert.assertTrue(merge(current, current, newVersion).hasNoConflicts());
        Assert.assertEquals(newValue2, current.referenceAttribute.get());
    }

    @Test
    public void test_delte(){
        ExampleFactoryA current = new ExampleFactoryA();
        ExampleFactoryB newValue = new ExampleFactoryB();
        current.referenceAttribute.set(newValue);

        ExampleFactoryA newVersion = new ExampleFactoryA();
        newVersion.referenceAttribute.set(null);

        Assert.assertTrue(merge(current, current, newVersion).hasNoConflicts());
        Assert.assertEquals(null, current.referenceAttribute.get());
    }


}