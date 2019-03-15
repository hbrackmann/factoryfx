package de.factoryfx.factory;

import de.factoryfx.data.Data;
import de.factoryfx.data.DataDictionary;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ReferenceBaseAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.builder.Scope;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 *  provides additional setup for attributes based on the FactoryTreeBuilder
 * @param <R> root
 */
public class FactoryTreeBuilderBasedAttributeSetup<L,R extends FactoryBase<L,R>,S> {

    private final FactoryTreeBuilder<L,R,S> factoryTreeBuilder;
    private BiFunction<?, ReferenceBaseAttribute<?, ?, ?>, List> newValuesProvider;

    @SuppressWarnings("unchecked")
    public FactoryTreeBuilderBasedAttributeSetup(FactoryTreeBuilder<L,R,S> factoryTreeBuilder) {
        this.factoryTreeBuilder = factoryTreeBuilder;

        newValuesProvider = (root, attribute) -> {
            Class<?> referenceClazz = ((ReferenceBaseAttribute<?,?, ?>) attribute).internal_getReferenceClass();
            List<?> newFactories =  this.createNewFactory((Class<FactoryBase<Object, R>>) referenceClazz);
            ArrayList result = new ArrayList(newFactories);

            if(result.isEmpty()){
                Class referenceClazzWorkaround=referenceClazz;
                result.add(DataDictionary.getDataDictionary(referenceClazzWorkaround).newInstance());
            }

            for (Object o : result) {
                FactoryBase<?,?> factory=(FactoryBase)o;
                for (Data child : factory.internal().collectChildrenDeepFromNode()) {
                    if (child instanceof FactoryBase<?,?>) {
                        child.internal().visitAttributesFlat((attributeVariableName, childAttribute) -> applyToAttribute(childAttribute));
                    }
                }
            }
            return result;
        };
    }

    public <LO, FO extends FactoryBase<LO, R>> List<FO> createNewFactory(Class<FO> clazz) {
        return factoryTreeBuilder.buildSubTrees(clazz);
    }

    @SuppressWarnings("unchecked")
    private void setupReferenceAttribute(FactoryReferenceAttribute<?, ?> referenceAttribute) {
        Class<?> referenceClass = referenceAttribute.internal_getReferenceClass();
        Scope scope = factoryTreeBuilder.getScope(referenceClass);
        if (scope== Scope.SINGLETON) {
            referenceAttribute.userNotSelectable();
        }
//        if (scope==Scope.PROTOTYPE) {
//            referenceAttribute.userNotSelectable();
//        }

        FactoryReferenceAttribute workaround =  referenceAttribute;
        workaround.newValuesProvider(newValuesProvider);
    }

    @SuppressWarnings("unchecked")
    private void setupReferenceListAttribute(FactoryReferenceListAttribute<?, ?> referenceAttribute) {
        Class<?> referenceClass = referenceAttribute.internal_getReferenceClass();
        Scope scope = factoryTreeBuilder.getScope(referenceClass);
        if (scope== Scope.SINGLETON) {
            referenceAttribute.userNotSelectable();
        }
//        if (scope==Scope.PROTOTYPE) {
//            referenceAttribute.userNotSelectable();
//        }
        FactoryReferenceListAttribute workaround =  referenceAttribute;
        workaround.newValuesProvider(newValuesProvider);
    }

    private void applyToAttribute(Attribute<?, ?> attribute) {
        if (attribute instanceof FactoryReferenceAttribute){
            setupReferenceAttribute((FactoryReferenceAttribute)attribute);
        }
        if (attribute instanceof FactoryReferenceListAttribute){
            setupReferenceListAttribute((FactoryReferenceListAttribute)attribute);
        }
    }

    public void applyToRootFactoryDeep(R root) {
        factoryTreeBuilder.fillFromExistingFactoryTree(root);

        root.internal().collectChildrenDeep().forEach(data -> {
            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> applyToAttribute(attribute));
        });
    }
}
