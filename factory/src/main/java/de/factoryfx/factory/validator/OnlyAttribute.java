package de.factoryfx.factory.validator;

import java.lang.reflect.Field;
import java.util.Optional;

import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.factory.FactoryBase;

public class OnlyAttribute implements FactoryStyleValidation {
    private final FactoryBase<?, ?> factoryBase;
    private final Field attributeField;

    public OnlyAttribute(FactoryBase<?, ?> factoryBase, Field attributeField) {
        this.factoryBase = factoryBase;
        this.attributeField = attributeField;
    }

    @Override
    public Optional<String> validateFactory() {
        if (!Attribute.class.isAssignableFrom(attributeField.getType())) {
            return Optional.of("Factories should only contains attribute and no state: "+ factoryBase.getClass().getName()+"#"+attributeField.getName());
        }
        return Optional.empty();
    }
}