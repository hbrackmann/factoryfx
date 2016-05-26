package de.factoryfx.model.merge;

import de.factoryfx.model.ClosedPreviousLiveObject;
import de.factoryfx.model.FactoryBase;
import de.factoryfx.model.attribute.AttributeMetadata;
import de.factoryfx.model.attribute.MapAttribute;
import de.factoryfx.model.testfactories.ExampleLiveObjectA;
import org.junit.Assert;
import org.junit.Test;

public class MapMergeTest extends MergeHelperTestBase{

    public static class MapExampleFactory extends FactoryBase<ExampleLiveObjectA,MapExampleFactory> {
        public final MapAttribute<String,String> mapAttribute =new MapAttribute(new AttributeMetadata<>(""));

        @Override
        protected ExampleLiveObjectA createImp(ClosedPreviousLiveObject<ExampleLiveObjectA> closedPreviousLiveObject) {
            return null;
        }
    }


    @Test
    public void test_same(){
        MapExampleFactory original = new MapExampleFactory();
        original.mapAttribute.get().put("k1","v1");
        original.mapAttribute.get().put("k2","v2");;
        original.mapAttribute.get().put("k3","v3");

        MapExampleFactory update = new MapExampleFactory();
        update.mapAttribute.get().put("k1","v1");
        update.mapAttribute.get().put("k2","v2");
        update.mapAttribute.get().put("k3","v3");

        Assert.assertTrue(merge(original, original, update).hasNoConflicts());
        Assert.assertEquals(3, original.mapAttribute.get().size());
        Assert.assertEquals("v1", original.mapAttribute.get().get("k1"));
        Assert.assertEquals("v2", original.mapAttribute.get().get("k2"));
        Assert.assertEquals("v3", original.mapAttribute.get().get("k3"));
    }

    @Test
    public void test_1_different(){
        MapExampleFactory original = new MapExampleFactory();
        original.mapAttribute.get().put("k1","v1");
        original.mapAttribute.get().put("k2","v2");;
        original.mapAttribute.get().put("k3","v3");

        MapExampleFactory update = new MapExampleFactory();
        update.mapAttribute.get().put("k1","v1");
        update.mapAttribute.get().put("k2","v2");
        update.mapAttribute.get().put("k3","v4");

        Assert.assertTrue(merge(original, original, update).hasNoConflicts());
        Assert.assertEquals(3, original.mapAttribute.get().size());
        Assert.assertEquals("v1", original.mapAttribute.get().get("k1"));
        Assert.assertEquals("v2", original.mapAttribute.get().get("k2"));
        Assert.assertEquals("v4", original.mapAttribute.get().get("k3"));
    }

    @Test
    public void test_1_deleted(){
        MapExampleFactory original = new MapExampleFactory();
        original.mapAttribute.get().put("k1","v1");
        original.mapAttribute.get().put("k2","v2");;
        original.mapAttribute.get().put("k3","v3");

        MapExampleFactory update = new MapExampleFactory();
        update.mapAttribute.get().put("k1","v1");
        update.mapAttribute.get().put("k2","v2");

        Assert.assertTrue(merge(original, original, update).hasNoConflicts());
        Assert.assertEquals(2, original.mapAttribute.get().size());
        Assert.assertEquals("v1", original.mapAttribute.get().get("k1"));
        Assert.assertEquals("v2", original.mapAttribute.get().get("k2"));

    }

}