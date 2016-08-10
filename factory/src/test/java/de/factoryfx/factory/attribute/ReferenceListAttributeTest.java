package de.factoryfx.factory.attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.testfactories.ExampleFactoryA;
import de.factoryfx.factory.testfactories.VoidLiveObject;
import javafx.collections.ObservableList;
import org.junit.Assert;
import org.junit.Test;

public class ReferenceListAttributeTest {

    public static class ExampleReferenceListFactory extends FactoryBase<VoidLiveObject,ExampleFactoryA> {
        public ReferenceListAttribute<ExampleFactoryA> referenceListAttribute =new ReferenceListAttribute<>(ExampleFactoryA.class,new AttributeMetadata());

        @Override
        protected VoidLiveObject createImp(Optional<VoidLiveObject> previousLiveObject) {
            return null;
        }

    }

    @Test
    public void testObservable(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ArrayList<String> calls= new ArrayList<>();
        exampleReferenceListFactory.referenceListAttribute.addListener((a,o)-> {
            calls.add("");
        });
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleFactoryA());

        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_json(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleFactoryA.stringAttribute.set("sadsasd");
        exampleReferenceListFactory.referenceListAttribute.get().add(exampleFactoryA);
        ObjectMapperBuilder.build().copy(exampleReferenceListFactory);
    }

    @Test
    public void remove_Listener(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ArrayList<String> calls= new ArrayList<>();
        AttributeChangeListener<ObservableList<ExampleFactoryA>> invalidationListener = (a,o) -> {
            calls.add("");
        };
        exampleReferenceListFactory.referenceListAttribute.addListener(invalidationListener);
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleFactoryA());

        Assert.assertEquals(1,calls.size());

        exampleReferenceListFactory.referenceListAttribute.removeListener(invalidationListener);
        exampleReferenceListFactory.referenceListAttribute.get().add(new ExampleFactoryA());
        Assert.assertEquals(1,calls.size());
    }

    @Test
    public void test_add_new(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        exampleReferenceListFactory.referenceListAttribute.addNewFactory(new ExampleReferenceListFactory());
        Assert.assertEquals(1,exampleReferenceListFactory.referenceListAttribute.size());

    }

    @Test
    public void test_possible_values(){
        ExampleReferenceListFactory exampleReferenceListFactory = new ExampleReferenceListFactory();
        ExampleFactoryA exampleFactoryA = new ExampleFactoryA();
        exampleReferenceListFactory.referenceListAttribute.add(exampleFactoryA);
        List<ExampleFactoryA> possibleFactories = exampleReferenceListFactory.referenceListAttribute.possibleValues(exampleReferenceListFactory);
        Assert.assertEquals(1,possibleFactories.size());
        Assert.assertEquals(exampleFactoryA,possibleFactories.get(0));

    }
}