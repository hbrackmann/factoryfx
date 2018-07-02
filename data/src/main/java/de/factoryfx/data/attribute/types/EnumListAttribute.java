package de.factoryfx.data.attribute.types;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.factoryfx.data.attribute.AttributeTypeInfo;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.data.util.LanguageText;

import java.util.*;
import java.util.function.Function;

/**
 * @param <E> enum class
 */
public class EnumListAttribute<E extends Enum<E>> extends ValueListAttribute<E,EnumListAttribute<E>> {

    private final Class<E> clazz;

    @SuppressWarnings("unchecked")
    public EnumListAttribute(Class<E> clazz) {
        super(clazz);//workaround for java generic bug
        this.clazz=clazz;
    }

    @Override
    public AttributeTypeInfo internal_getAttributeType() {
        return new AttributeTypeInfo(clazz,null,null,AttributeTypeInfo.AttributeTypeCategory.VALUE);
    }

    public List<Enum<E>> internal_possibleEnumValues() {
        return new ArrayList<>(Arrays.asList(clazz.getEnumConstants()));
    }

    @JsonIgnore
    private EnumTranslations<E> enumTranslations;

    public EnumListAttribute<E> deEnum(E value, String text){
        if (enumTranslations==null){
            enumTranslations = new EnumTranslations<>();
        }
        enumTranslations.deEnum(value,text);
        return this;
    }

    public EnumListAttribute<E> enEnum(E value, String text){
        if (enumTranslations==null){
            enumTranslations = new EnumTranslations<>();
        }
        enumTranslations.enEnum(value,text);
        return this;
    }

    public String internal_enumDisplayText(Enum<?> enumValue,Function<LanguageText,String> uniformDesign){
        if (enumValue==null){
            return "-";
        }
        if (enumTranslations!=null){
            return enumTranslations.getDisplayText(enumValue,uniformDesign);
        }
        return enumValue.name();
    }


}