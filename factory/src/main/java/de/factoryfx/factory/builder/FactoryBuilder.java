package de.factoryfx.factory.builder;

import de.factoryfx.factory.FactoryBase;

import java.util.HashSet;
import java.util.function.Function;

public class FactoryBuilder<V, L, R extends FactoryBase<L,V>> {
    private final FactoryContext<V> factoryContext = new FactoryContext<>();
    private final Class<R> root;

    public FactoryBuilder(Class<R> root) {
        this.root = root;
    }


    public <L, F extends FactoryBase<L,V>> void addFactory(Class<F> clazz, Scope scope, Function<SimpleFactoryContext<V>, F> creator){
        factoryContext.addFactoryCreator(new FactoryCreator<>(clazz,scope,creator));
    }

    public <L, F extends FactoryBase<L,V>> void addFactory(Class<F> clazz, Scope scope){
        addFactory(clazz,scope,new DefaultCreator<>(clazz));
    }

    public R build(){
        return factoryContext.get(root,new HashSet<>());
    }
}