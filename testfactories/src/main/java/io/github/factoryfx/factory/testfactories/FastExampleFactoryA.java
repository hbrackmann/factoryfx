package io.github.factoryfx.factory.testfactories;

import io.github.factoryfx.factory.*;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryListAttribute;
import io.github.factoryfx.factory.fastfactory.FastFactoryAttribute;
import io.github.factoryfx.factory.fastfactory.FastFactoryListAttribute;
import io.github.factoryfx.factory.fastfactory.FastFactoryUtility;
import io.github.factoryfx.factory.fastfactory.FastValueAttribute;

import java.util.ArrayList;
import java.util.List;

public class FastExampleFactoryA extends SimpleFactoryBase<ExampleLiveObjectA,FastExampleFactoryA> {
    public String stringAttribute;
    public FastExampleFactoryB referenceAttribute;
    public List<FastExampleFactoryB> referenceListAttribute=new ArrayList<>();

    @Override
    protected ExampleLiveObjectA createImpl() {
        return new ExampleLiveObjectA(FastFactoryUtility.instance(referenceAttribute), FastFactoryUtility.instances(referenceListAttribute));
    }

    static {

        FastFactoryUtility.setup(FastExampleFactoryA.class,
            new FastFactoryUtility<>(()->
                    List.of(
                            new FastValueAttribute<>(
                                ()->new StringAttribute().labelText("ExampleA1"),
                                (factory) -> factory.stringAttribute,
                                (factory, value) -> factory.stringAttribute = value,
                                "stringAttribute"
                            )
                            ,
                            new FastFactoryAttribute<>(
                                    ()->new FactoryAttribute<ExampleLiveObjectB, FastExampleFactoryB>().labelText("ExampleA2"),
                                    (factory) -> factory.referenceAttribute,
                                    (factory, value) -> factory.referenceAttribute = value,
                                    FastExampleFactoryB.class,
                                    ExampleLiveObjectB.class,
                                    "referenceAttribute"
                            )
                            ,
                            new FastFactoryListAttribute<>(
                                    ()->new FactoryListAttribute<ExampleLiveObjectB, FastExampleFactoryB>().labelText("ExampleA3"),
                                    (factory) -> factory.referenceListAttribute,
                                    (factory, value) -> {
                                        factory.referenceListAttribute = value;
                                    },
                                    FastExampleFactoryB.class,
                                    ExampleLiveObjectB.class,
                                    "referenceListAttribute"
                            )
                    )
        ));
    }

    public FastExampleFactoryA(){

    }



}
