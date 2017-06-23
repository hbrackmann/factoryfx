package de.factoryfx.docu.dependencyinjection;

import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;

public class RootFactory extends SimpleFactoryBase<Root,Void>{
    public final FactoryReferenceAttribute<Dependency,DependencyFactory> dependency =new FactoryReferenceAttribute<>(DependencyFactory.class).labelText("dependency");

    @Override
    public Root createImpl() {
        return new Root(dependency.instance());
    }
}