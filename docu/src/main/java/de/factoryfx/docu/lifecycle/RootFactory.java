package de.factoryfx.docu.lifecycle;

import de.factoryfx.factory.FactoryBase;

public class RootFactory extends FactoryBase<Root, RootFactory> {

    public RootFactory(){
        configLifeCycle().setCreator(Root::new);
        configLifeCycle().setReCreator(oldRoot -> new Root());
        configLifeCycle().setStarter(newRoot -> newRoot.start());
        configLifeCycle().setDestroyer(oldRoot -> oldRoot.destroy());
    }
}
