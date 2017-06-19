package de.factoryfx.javafx.editor.attribute;

import java.math.BigDecimal;
import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;

import javafx.scene.control.TableView;
import javafx.scene.paint.Color;

import org.controlsfx.glyphfont.FontAwesome;

import com.google.common.base.Ascii;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.ReferenceAttribute;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.attribute.ValueListAttribute;
import de.factoryfx.data.attribute.ViewListReferenceAttribute;
import de.factoryfx.data.attribute.ViewReferenceAttribute;
import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.primitive.DoubleAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.primitive.LongAttribute;
import de.factoryfx.data.attribute.types.BigDecimalAttribute;
import de.factoryfx.data.attribute.types.ColorAttribute;
import de.factoryfx.data.attribute.time.DurationAttribute;
import de.factoryfx.data.attribute.types.EncryptedString;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.attribute.types.EnumAttribute;
import de.factoryfx.data.attribute.types.I18nAttribute;
import de.factoryfx.data.attribute.time.LocalDateAttribute;
import de.factoryfx.data.attribute.time.LocalDateTimeAttribute;
import de.factoryfx.data.attribute.types.LocaleAttribute;
import de.factoryfx.data.attribute.types.ObjectValueAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.data.attribute.types.URIAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.editor.attribute.builder.DataSingleAttributeEditorBuilder;
import de.factoryfx.javafx.editor.attribute.builder.NoListSingleAttributeEditorBuilder;
import de.factoryfx.javafx.editor.attribute.builder.SimpleSingleAttributeEditorBuilder;
import de.factoryfx.javafx.editor.attribute.builder.SingleAttributeEditorBuilder;
import de.factoryfx.javafx.editor.attribute.visualisation.BigDecimalAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.BooleanAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ColorAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.DoubleAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.DurationAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.EncryptedStringAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.EnumAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ExpandableAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.I18nAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.IntegerAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.LocalDateAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.LocalDateTimeAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.LocaleAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.LongAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ObjectValueAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ReferenceAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ReferenceListAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.StringAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.StringLongAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.URIAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ViewListReferenceAttributeVisualisation;
import de.factoryfx.javafx.editor.attribute.visualisation.ViewReferenceAttributeVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.datalistedit.DataListEditWidget;

public class AttributeEditorBuilder {

    private final List<SingleAttributeEditorBuilder<?>> singleAttributeEditorBuilders;

    public AttributeEditorBuilder(List<SingleAttributeEditorBuilder<?>> singleAttributeEditorBuilders) {
        this.singleAttributeEditorBuilders = singleAttributeEditorBuilders;
    }

    public static List<SingleAttributeEditorBuilder<?>> createDefaultSingleAttributeEditorBuilders(UniformDesign uniformDesign){
        ArrayList<SingleAttributeEditorBuilder<?>> result = new ArrayList<>();
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,BigDecimalAttribute.class,BigDecimal.class,(attribute)-> new BigDecimalAttributeVisualisation(attribute.internal_getDecimalFormatPattern()),()->new BigDecimalAttribute()));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,BooleanAttribute.class,Boolean.class,(attribute)-> new BooleanAttributeVisualisation(),()->new BooleanAttribute()));
//        result.add(new SimpleSingleAttributeEditorBuilder<>(ByteArrayAttribute.class,byte[].class,(attribute)->{
//            return new AttributeEditor<BigDecimal>(attribute,new BigDecimalAttributeVisualisation(attribute.internal_getDecimalFormatPattern()),uniformDesign);
//        }));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,ColorAttribute.class,Color.class,(attribute)-> new ColorAttributeVisualisation(),()->new ColorAttribute()));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,DoubleAttribute.class,Double.class,(attribute)-> new DoubleAttributeVisualisation(),()->new DoubleAttribute()));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,EncryptedStringAttribute.class,EncryptedString.class,(attribute)-> new EncryptedStringAttributeVisualisation(attribute::createKey, attribute::isValidKey,uniformDesign),()->new EncryptedStringAttribute()));


        result.add(new NoListSingleAttributeEditorBuilder<Object,ObjectValueAttribute<?>>(uniformDesign,(attribute)->attribute instanceof ObjectValueAttribute,(attribute)->new ObjectValueAttributeVisualisation()));
        result.add(new NoListSingleAttributeEditorBuilder<Enum,EnumAttribute<?>>(uniformDesign,(attribute)->attribute instanceof EnumAttribute,(attribute)->new EnumAttributeVisualisation(attribute.internal_possibleEnumValues())));

        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,I18nAttribute.class,LanguageText.class,(attribute)-> new I18nAttributeVisualisation(),()->new I18nAttribute()));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,IntegerAttribute.class,Integer.class,(attribute)-> new IntegerAttributeVisualisation(),()->new IntegerAttribute()));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,LocalDateAttribute.class,LocalDate.class,(attribute)-> new LocalDateAttributeVisualisation(),()->new LocalDateAttribute()));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,LocalDateTimeAttribute.class,LocalDateTime.class,(attribute)-> new LocalDateTimeAttributeVisualisation(),()->new LocalDateTimeAttribute()));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,LocaleAttribute.class,Locale.class,(attribute)-> new LocaleAttributeVisualisation(),()->new LocaleAttribute()));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,LongAttribute.class,Long.class,(attribute)-> new LongAttributeVisualisation(),()->new LongAttribute()));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign, DurationAttribute.class, Duration.class, (attribute)-> new DurationAttributeVisualisation(), ()->new DurationAttribute()));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,StringAttribute.class,String.class,(attribute)->{
            if (attribute.internal_isLongText()){
                return new ExpandableAttributeVisualisation<>(new StringLongAttributeVisualisation(),uniformDesign, (s)->Ascii.truncate(s,20,"..."),FontAwesome.Glyph.FONT,attribute.internal_isDefaultExpanded() );
            } else {
                return new StringAttributeVisualisation();
            }
        },()->new StringAttribute()));
        result.add(new SimpleSingleAttributeEditorBuilder<>(uniformDesign,URIAttribute.class,URI.class,(attribute)-> new URIAttributeVisualisation(),()->new URIAttribute()));
        result.add(new DataSingleAttributeEditorBuilder(uniformDesign,(a)->a instanceof ViewReferenceAttribute,(attribute, dataEditor, previousData)-> new ViewReferenceAttributeVisualisation(dataEditor, uniformDesign)));
        result.add(new DataSingleAttributeEditorBuilder(uniformDesign,(a)->a instanceof ViewListReferenceAttribute,(attribute, dataEditor, previousData)->{
            ViewListReferenceAttributeVisualisation visualisation = new ViewListReferenceAttributeVisualisation(dataEditor, uniformDesign);
            ExpandableAttributeVisualisation<List<Data>> expandableAttributeVisualisation= new ExpandableAttributeVisualisation<>(visualisation,uniformDesign,(l)->"Items: "+l.size(),FontAwesome.Glyph.LIST);
            if (((ViewListReferenceAttribute)attribute).get().contains(previousData)){
                expandableAttributeVisualisation.expand();
            }
            return expandableAttributeVisualisation;
        }));

        result.add(new SingleAttributeEditorBuilder<Data>(){
            @Override
            public boolean isEditorFor(Attribute<?, ?> attribute) {
                return attribute instanceof ReferenceAttribute;
            }

            @Override
            public AttributeEditor<Data, ?> createEditor(Attribute<?, ?> attribute, DataEditor dataEditor, Data previousData) {
                ReferenceAttribute referenceAttribute = (ReferenceAttribute) attribute;
                return new AttributeEditor<>(referenceAttribute,new ReferenceAttributeVisualisation(uniformDesign,dataEditor, referenceAttribute::internal_addNewFactory, referenceAttribute::internal_possibleValues, referenceAttribute::internal_deleteFactory, referenceAttribute.internal_isUserEditable(),referenceAttribute.internal_isUserSelectable(),referenceAttribute.internal_isUserCreatable()),uniformDesign);

            }
        });

        result.add(new SingleAttributeEditorBuilder<List<Data>>(){
            @Override
            public boolean isEditorFor(Attribute<?, ?> attribute) {
                return attribute instanceof ReferenceListAttribute;
            }

            @Override
            public AttributeEditor<List<Data>, ?> createEditor(Attribute<?, ?> attribute, DataEditor dataEditor, Data previousData) {
                ReferenceListAttribute referenceListAttribute = (ReferenceListAttribute)attribute;
                final TableView<Data> dataTableView = new TableView<>();
                final ReferenceListAttributeVisualisation referenceListAttributeVisualisation = new ReferenceListAttributeVisualisation(uniformDesign, dataEditor, dataTableView, new DataListEditWidget<>(referenceListAttribute.get(), dataTableView, dataEditor,uniformDesign,referenceListAttribute));
                ExpandableAttributeVisualisation<List<Data>> expandableAttributeVisualisation= new ExpandableAttributeVisualisation<>(referenceListAttributeVisualisation,uniformDesign,(l)->"Items: "+l.size(),FontAwesome.Glyph.LIST);
                if (referenceListAttribute.get().contains(previousData)){
                    expandableAttributeVisualisation.expand();
                }
                return new AttributeEditor<>(referenceListAttribute,expandableAttributeVisualisation,uniformDesign);

            }
        });


        return result;
    }

    public Optional<AttributeEditor<?,?>> getAttributeEditor(Attribute<?,?> attribute, DataEditor dataEditor, Supplier<List<ValidationError>> validation, Data oldValue){

        if (attribute instanceof ValueListAttribute<?,?>){
            Optional<SingleAttributeEditorBuilder<?>> builder = singleAttributeEditorBuilders.stream().filter(a -> a.isListItemEditorFor(attribute)).findAny();
            return builder.map(singleAttributeEditorBuilder -> singleAttributeEditorBuilder.createValueListEditor(attribute));
        }

        Optional<SingleAttributeEditorBuilder<?>> builder = singleAttributeEditorBuilders.stream().filter(a -> a.isEditorFor(attribute)).findAny();
        return builder.map(singleAttributeEditorBuilder -> singleAttributeEditorBuilder.createEditor(attribute, dataEditor, oldValue));
    }

}
