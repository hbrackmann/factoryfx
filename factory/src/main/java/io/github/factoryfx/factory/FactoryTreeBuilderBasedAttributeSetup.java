package io.github.factoryfx.factory;

import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceListAttribute;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.factory.metadata.FactoryMetadata;
import io.github.factoryfx.factory.metadata.FactoryMetadataManager;

import java.util.ArrayList;
import java.util.List;

/**
 *  provides additional setup for attributes based on the FactoryTreeBuilder
 * @param <R> root
 */
public class FactoryTreeBuilderBasedAttributeSetup<L,R extends FactoryBase<L,R>,S> {

    private final FactoryTreeBuilder<L,R,S> factoryTreeBuilder;

    public FactoryTreeBuilderBasedAttributeSetup(FactoryTreeBuilder<L,R,S> factoryTreeBuilder) {
        this.factoryTreeBuilder = factoryTreeBuilder;
    }

    public <LO, FO extends FactoryBase<LO, R>> List<FO> createNewFactory(Class<FO> clazz) {
        List<FO> newFactories =  factoryTreeBuilder.buildSubTrees(clazz);
        ArrayList<FO> result = new ArrayList<>(newFactories);
        if(result.isEmpty()){
            FactoryMetadata<R,LO, FO> factoryMetadata = FactoryMetadataManager.getMetadata(clazz);
            FO instance = factoryMetadata.newInstance();
            factoryMetadata.setAttributeReferenceClasses(instance);
            result.add(instance);
        }
        return result;
    }

    private void setupReferenceAttribute(FactoryReferenceAttribute<?, ?, ?> referenceAttribute) {
        Class<?> referenceClass = referenceAttribute.internal_getReferenceClass();
        Scope scope = factoryTreeBuilder.getScope(referenceClass);
        if (scope== Scope.SINGLETON) {
            referenceAttribute.userNotSelectable();
        }
//        if (scope==Scope.PROTOTYPE) {
//            referenceAttribute.userNotSelectable();
//        }
    }

    private void setupReferenceListAttribute(FactoryReferenceListAttribute<?, ?, ?> referenceAttribute) {
        Class<?> referenceClass = referenceAttribute.internal_getReferenceClass();
        Scope scope = factoryTreeBuilder.getScope(referenceClass);
        if (scope== Scope.SINGLETON) {
            referenceAttribute.userNotSelectable();
        }
//        if (scope==Scope.PROTOTYPE) {
//            referenceAttribute.userNotSelectable();
//        }
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
        root.internal().serFactoryTreeBuilderBasedAttributeSetupForRoot(this);
        factoryTreeBuilder.fillFromExistingFactoryTree(root);

        root.internal().collectChildrenDeep().forEach(data -> {
            data.internal().visitAttributesFlat((attributeVariableName, attribute) -> applyToAttribute(attribute));
        });
    }
}