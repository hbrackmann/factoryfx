package de.factoryfx.factory.atrribute;

import java.util.*;
import java.util.function.Predicate;

import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.factory.FactoryBase;

/**
 * Attribute with factory
 * @param <L> liveobject created form the factory
 * @param <F> factory
 */
public class FactoryReferenceListAttribute<L, F extends FactoryBase<? extends L,?,?>> extends  ReferenceListAttribute<F,FactoryReferenceListAttribute<L, F>>{


    public FactoryReferenceListAttribute() {
        super();
    }

    public FactoryReferenceListAttribute(Class<F> clazz) {
        super();
        setup(clazz);
    }

    public List<L> instances(){
        ArrayList<L> result = new ArrayList<>(this.size());
        for(F item: this){
            result.add(item.internalFactory().instance());
        }
        return result;
    }

    public L instances(Predicate<F> filter){
        Optional<F> any = get().stream().filter(filter).findAny();
        return any.map(t -> t.internalFactory().instance()).orElse(null);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> setupUnsafe(Class clazz) {
        return super.setupUnsafe(clazz);
    }

    @Override
    public FactoryReferenceListAttribute<L, F> setup(Class<F> clazz) {
        return super.setup(clazz);
    }

}
