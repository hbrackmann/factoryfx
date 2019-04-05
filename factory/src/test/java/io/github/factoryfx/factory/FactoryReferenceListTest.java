package io.github.factoryfx.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.github.factoryfx.factory.storage.DataUpdate;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;
import io.github.factoryfx.factory.log.FactoryUpdateLog;
import io.github.factoryfx.server.Microservice;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FactoryReferenceListTest {

    private static List<String> creator = new ArrayList<>();
    private static List<String> recreator = new ArrayList<>();
    private static List<String> starter = new ArrayList<>();
    private static List<String> destroyer = new ArrayList<>();

    public static class ObjectFactory extends FactoryBase<Object, RootFactory> {
        ObjectFactory() {
            configLifeCycle().setCreator(() -> {
                System.out.println("creator called " + getId());
                FactoryReferenceListTest.creator.add(getId());
                return new Object();
            });
            configLifeCycle().setReCreator(old -> {
                System.out.println("reCreator called " + getId());
                FactoryReferenceListTest.recreator.add(getId());
                return new Object();
            });
            configLifeCycle().setStarter(i -> {
                System.out.println("starter called " + getId());
                starter.add(getId());
            });
            configLifeCycle().setDestroyer(i -> {
                System.out.println("destroyer called " + getId());
                destroyer.add(getId());
            });
        }
    }

    public static class RootFactory extends SimpleFactoryBase<String, RootFactory> {
        public final FactoryReferenceListAttribute<RootFactory,Object, ObjectFactory> objects = new FactoryReferenceListAttribute<>();

        @Override
        public String createImpl() {
            return objects.instances().stream().map(Object::toString).collect(Collectors.joining(", "));
        }
    }

    @Test
    public void referenceListTest() {
        ObjectFactory first = new ObjectFactory();

        FactoryTreeBuilder< String,RootFactory, Void> builder = new FactoryTreeBuilder<>(RootFactory.class, ctx->{
            RootFactory rootFactory = new RootFactory();
            rootFactory.objects.add(first);
            return rootFactory;
        });

        Microservice<String,RootFactory, Void> microService = builder.microservice().withInMemoryStorage().build();
        microService.start();

        {
            Assertions.assertTrue(creator.contains(first.getId()));
            creator.clear();
            recreator.clear();
            starter.clear();
            destroyer.clear();

            System.out.println("update started");
            DataUpdate<RootFactory> update1 = microService.prepareNewFactory();
            ObjectFactory second = new ObjectFactory();
            update1.root.objects.add(second);

            FactoryUpdateLog<RootFactory> res1 = microService.updateCurrentFactory(update1);
            Assertions.assertTrue(res1.successfullyMerged());

             Assertions.assertFalse(creator.contains(first.getId()));
             Assertions.assertFalse(starter.contains(first.getId()));
             Assertions.assertFalse(destroyer.contains(first.getId()));

            Assertions.assertFalse(recreator.contains(first.getId()));
            Assertions.assertTrue(creator.contains(second.getId()));
            Assertions.assertTrue(starter.contains(second.getId()));
        }

    }

}