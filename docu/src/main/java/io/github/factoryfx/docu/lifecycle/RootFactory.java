package io.github.factoryfx.docu.lifecycle;

import io.github.factoryfx.factory.FactoryBase;

public class RootFactory extends FactoryBase<Root, RootFactory> {

    public RootFactory(){
        configLifeCycle().setCreator(Root::new);
        configLifeCycle().setReCreator(oldRoot -> new Root());
        configLifeCycle().setStarter(Root::start);
        configLifeCycle().setDestroyer(Root::destroy);
    }
}
