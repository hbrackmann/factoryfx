package io.github.factoryfx.docu.runtimestatus;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FactoryBase;


public class RootFactory extends FactoryBase<Root, RootFactory> {
    public final StringAttribute stringAttribute = new StringAttribute();

    public RootFactory(){
        configLifeCycle().setCreator(() ->  new Root(stringAttribute.get()));
        configLifeCycle().setReCreator(previousLiveObject -> new Root(stringAttribute.get(),previousLiveObject.getCounter()));
    }
}
