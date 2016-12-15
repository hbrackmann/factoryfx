package de.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import org.junit.Assert;
import org.junit.Test;

public class FactoryManagerLivecycleTest {

    public static class  ExampleLiveObjectA{
        public final String string;
        public final ExampleLiveObjectA ref;

        public ExampleLiveObjectA(String string, ExampleLiveObjectA ref) {
            this.string = string;
            this.ref = ref;
        }
    }


    public static class ExampleFactoryA extends FactoryBase<ExampleLiveObjectA,Void> {
        public final FactoryReferenceAttribute<ExampleLiveObjectA,ExampleFactoryB> ref = new FactoryReferenceAttribute<>(ExampleFactoryB.class,new AttributeMetadata());


        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();

        public ExampleFactoryA(){
            configLiveCycle().setCreator(() -> {
                createCalls.add("created");
                return new ExampleLiveObjectA("",ref.instance());
            });
            configLiveCycle().setReCreator(exampleLiveObjectA -> {
                reCreateCalls.add("created");
                return new ExampleLiveObjectA("",ref.instance());
            });
            configLiveCycle().setDestroyer(exampleLiveObjectA -> destroyCalls.add("created"));
            configLiveCycle().setStarter(exampleLiveObjectA -> startCalls.add("created"));
        }

        public void resetCounter(){
            createCalls.clear();
            reCreateCalls.clear();
            startCalls.clear();
            destroyCalls.clear();
        }
    }

    public static class ExampleFactoryB extends FactoryBase<ExampleLiveObjectA,Void> {

        public List<String> createCalls= new ArrayList<>();
        public List<String> reCreateCalls= new ArrayList<>();
        public List<String> startCalls= new ArrayList<>();
        public List<String> destroyCalls= new ArrayList<>();
        public StringAttribute stringAttribute=new StringAttribute(new AttributeMetadata());

        public ExampleFactoryB(){
            configLiveCycle().setCreator(() -> {
                createCalls.add("created");
                return new ExampleLiveObjectA("",null);
            });
            configLiveCycle().setReCreator(exampleLiveObjectA -> {
                reCreateCalls.add("created");
                return exampleLiveObjectA;
            });
            configLiveCycle().setDestroyer(exampleLiveObjectA -> destroyCalls.add("created"));
            configLiveCycle().setStarter(exampleLiveObjectA -> startCalls.add("created"));
        }

        public void resetCounter(){
            createCalls.clear();
            reCreateCalls.clear();
            startCalls.clear();
            destroyCalls.clear();
        }
    }


    @Test
    public void test_initial_start(){
        FactoryManager<ExampleLiveObjectA,Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        final ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        factoryManager.start(exampleFactoryA);

        Assert.assertEquals(1,exampleFactoryA.createCalls.size());
        Assert.assertEquals(1,exampleFactoryB.createCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
        Assert.assertEquals(1,exampleFactoryB.startCalls.size());
    }

    @Test
    public void test_initial_destroy(){
        FactoryManager<ExampleLiveObjectA,Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        final ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        factoryManager.start(exampleFactoryA);
        factoryManager.stop();

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assert.assertEquals(1,exampleFactoryB.destroyCalls.size());
    }

    @Test
    public void test_initial_changed(){
        FactoryManager<ExampleLiveObjectA,Void,ExampleFactoryA> factoryManager = new FactoryManager<>();

        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        final ExampleFactoryB exampleFactoryB = new ExampleFactoryB();
        exampleFactoryA.ref.set(exampleFactoryB);

        factoryManager.start(exampleFactoryA);

        ExampleFactoryA common = factoryManager.getCurrentFactory().internal().copy();
        ExampleFactoryA update = factoryManager.getCurrentFactory().internal().copy();
        update.ref.get().stringAttribute.set("changed");

        exampleFactoryA.resetCounter();
        exampleFactoryB.resetCounter();
        factoryManager.update(common,update);

        Assert.assertEquals(1,exampleFactoryA.destroyCalls.size());
        Assert.assertEquals(1,exampleFactoryB.destroyCalls.size());

        Assert.assertEquals(1,exampleFactoryA.reCreateCalls.size());
        Assert.assertEquals(1,exampleFactoryB.reCreateCalls.size());

        Assert.assertEquals(1,exampleFactoryA.startCalls.size());
        Assert.assertEquals(1,exampleFactoryB.startCalls.size());
    }

}