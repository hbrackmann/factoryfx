package io.github.factoryfx.factory.attribute.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.*;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ViewListReferenceAttributeTest {
    public static class ViewListExampleFactory extends FactoryBase<Void, ViewListExampleFactoryRoot> {

        public final StringAttribute forFilter= new StringAttribute();
        public final FactoryViewListReferenceAttribute<ViewListExampleFactoryRoot,Void,ViewExampleDataA> view= new FactoryViewListReferenceAttribute<>( (ViewListExampleFactoryRoot viewExampleFactoryRoot)->{
                return viewExampleFactoryRoot.list.get().stream().filter(exampleFactoryA -> exampleFactoryA.stringAttribute.get().equals(forFilter.get())).collect(Collectors.toList());
            }
        );

    }

    public static class ViewListExampleFactoryRoot extends FactoryBase<Void, ViewListExampleFactoryRoot>{
        public final FactoryReferenceAttribute<ViewListExampleFactoryRoot,Void,ViewListExampleFactory> ref = new FactoryReferenceAttribute<>();
        public final FactoryReferenceListAttribute<ViewListExampleFactoryRoot,Void,ViewExampleDataA> list= new FactoryReferenceListAttribute<>();
    }
    
    public static class ViewExampleDataA extends FactoryBase<Void, ViewListExampleFactoryRoot>{
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1");
    }

    @Test
    public void test(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("1");
            root.list.add(value);
        }
        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }

        root = root.internal().addBackReferences();

        Assertions.assertEquals(1,root.ref.get().view.get().size());
        Assertions.assertEquals("1",root.ref.get().view.get().get(0).stringAttribute.get());
    }

    @Test
    public void test_json(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("1");
            root.list.add(value);
        }
        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }

        root = root.internal().addBackReferences();
        final String valueAsString = ObjectMapperBuilder.build().writeValueAsString(root);
        Assertions.assertFalse(valueAsString.contains("view"));
    }

    @Test
    public void test_nomatch(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }
        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("3");
            root.list.add(value);
        }

        root = root.internal().addBackReferences();

        Assertions.assertEquals(0,root.ref.get().view.get().size());
    }

    @Test
    public void test_multimatch(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("1");
            root.list.add(value);
        }
        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("1");
            root.list.add(value);
        }

        root = root.internal().addBackReferences();

        Assertions.assertEquals(2,root.ref.get().view.get().size());
    }


    @Test
    public void test_change_listener(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }
        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("3");
            root.list.add(value);
        }

        root = root.internal().addBackReferences();
        root.ref.get().view.setRunlaterExecutor(runnable -> runnable.run());

        ArrayList<String> calls=new ArrayList<>();
        root.ref.get().view.internal_addListener((attribute, value1) -> {
            if (!value1.isEmpty()){
                calls.add("1");
            } else {
                calls.add("empty");
            }
        });

        root.ref.get().forFilter.set("2");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(1,root.ref.get().view.get().size());
        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals("1",calls.get(0));


        calls.clear();
        root.ref.get().forFilter.set("1");
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Assertions.assertEquals(1,calls.size());
        Assertions.assertEquals("empty",calls.get(0));
    }


    @Test
    public void test_change_listener_no_changes(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }
        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("3");
            root.list.add(value);
        }

        root.internal().addBackReferences();

        ArrayList<String> calls=new ArrayList<>();
        viewExampleFactory.view.internal_addListener((attribute, value1) -> calls.add("1"));

        viewExampleFactory.forFilter.set("1");

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Assertions.assertEquals(0,calls.size());
    }

    @Test
    public void test_reconstructMetadataDeepRoot(){
        ViewListExampleFactory viewExampleFactory=new ViewListExampleFactory();
        viewExampleFactory.forFilter.set("1");

        ViewListExampleFactoryRoot root = new ViewListExampleFactoryRoot();
        root.ref.set(viewExampleFactory);

        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("2");
            root.list.add(value);
        }
        {
            ViewExampleDataA value = new ViewExampleDataA();
            value.stringAttribute.set("3");
            root.list.add(value);
        }

        root.internal().addBackReferences();
    }

    @Test
    public void removeListener() throws Exception {
        FactoryViewListReferenceAttribute<ViewListExampleFactoryRoot, Void, ViewExampleDataA> attribute = new FactoryViewListReferenceAttribute<>((ViewListExampleFactoryRoot viewExampleFactoryRoot) -> {
            return viewExampleFactoryRoot.list.filtered((exampleFactoryA -> exampleFactoryA.stringAttribute.get().equals("")));
        });

        final AttributeChangeListener<List<ViewExampleDataA>, FactoryViewListReferenceAttribute<ViewListExampleFactoryRoot, Void, ViewExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }

    @Test
    public void removeWeakListener() throws Exception {
        FactoryViewListReferenceAttribute<ViewListExampleFactoryRoot, Void, ViewExampleDataA> attribute = new FactoryViewListReferenceAttribute<>((ViewListExampleFactoryRoot viewExampleFactoryRoot) -> {
            return viewExampleFactoryRoot.list.filtered((exampleFactoryA -> exampleFactoryA.stringAttribute.get().equals("")));
        });

        final AttributeChangeListener<List<ViewExampleDataA>, FactoryViewListReferenceAttribute<ViewListExampleFactoryRoot, Void, ViewExampleDataA>> attributeChangeListener = (a, value) -> System.out.println(value);
        attribute.internal_addListener(new WeakAttributeChangeListener<>(attributeChangeListener));
        Assertions.assertTrue(attribute.listeners.size()==1);
        attribute.internal_removeListener(attributeChangeListener);
        Assertions.assertTrue(attribute.listeners.size()==0);
    }
}