package io.github.factoryfx.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.CopySemantic;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.factory.attribute.dependency.ReferenceBaseAttribute;
import io.github.factoryfx.factory.attribute.types.ObjectValueAttribute;
import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.jackson.ObjectMapperBuilder;
import io.github.factoryfx.factory.jackson.SimpleObjectMapper;
import io.github.factoryfx.factory.merge.DataMerger;
import io.github.factoryfx.factory.merge.testdata.ExampleDataA;
import io.github.factoryfx.factory.merge.testdata.ExampleDataB;
import io.github.factoryfx.factory.merge.testdata.ExampleDataC;
import io.github.factoryfx.factory.storage.migration.metadata.DataStorageMetadataDictionary;
import io.github.factoryfx.factory.util.LanguageText;
import io.github.factoryfx.factory.validation.AttributeValidation;
import io.github.factoryfx.factory.validation.ValidationResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;



public class DataTest {

    @Test
    public void test_visitAttributes(){
        ExampleDataA testModel = new ExampleDataA();
        testModel.stringAttribute.set("xxxx");
        testModel.referenceAttribute.set(new ExampleDataB());

        ArrayList<String> calls = new ArrayList<>();
        testModel.internal().visitAttributesFlat((attributeVariableName, attribute) -> calls.add(attribute.get().toString()));
        Assertions.assertEquals(3,calls.size());
        Assertions.assertEquals("xxxx",calls.get(0));
    }


    public static class ExampleFactoryThis extends FactoryBase<Void,ExampleFactoryThis> {
        ArrayList<UUID> calls=new ArrayList<>();
        public final StringAttribute stringAttribute= new StringAttribute().labelText("ExampleA1").validation(value -> {
            calls.add(ExampleFactoryThis.this.getId());
            return new ValidationResult(false,new LanguageText());
        });

        public ExampleFactoryThis(){
            System.out.println();        }
    }


    @Test
    public void test_reconstructMetadataDeepRoot_this() throws IOException {
        ExampleFactoryThis exampleFactoryThis = new ExampleFactoryThis();

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        String string = mapper.writeValueAsString(exampleFactoryThis);
        ExampleFactoryThis readed = ObjectMapperBuilder.buildNewObjectMapper().readValue(string,ExampleFactoryThis.class);

//        Assertions.assertEquals(null,readed.stringAttribute);
        Assertions.assertEquals(0,readed.calls.size());

        readed.internal().finalise();

        readed.internal().validateFlat();

        Assertions.assertNotNull(readed.stringAttribute);
        Assertions.assertEquals(1,readed.calls.size());
        Assertions.assertEquals(readed.getId(),readed.calls.get(0));
    }


    @Test
    @SuppressWarnings("unchecked")
    public void test_reconstructMetadataDeepRoot_displaytext() throws Exception {
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        ExampleDataC exampleFactoryC = new ExampleDataC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        String string = mapper.writeValueAsString(exampleFactoryA);
        ExampleDataA readed = ObjectMapperBuilder.buildNewObjectMapper().readValue(string,ExampleDataA.class);

        readed.internal().finalise();


        final Field displayTextDependencies = FactoryBase.class.getDeclaredField("displayTextDependencies");
        displayTextDependencies.setAccessible(true);
        final List<Attribute<?,?>> attributes = (List<Attribute<?,?>>) displayTextDependencies.get(readed);
        Assertions.assertEquals(1, attributes.size());
        Assertions.assertEquals(readed.stringAttribute, attributes.get(0));


        final Field dataValidations = FactoryBase.class.getDeclaredField("dataValidations");
        dataValidations.setAccessible(true);
        displayTextDependencies.setAccessible(true);
        final List<AttributeValidation<?>> dataValidationsAttributes = (List<AttributeValidation<?>>) dataValidations.get(readed);
        Assertions.assertEquals(1, dataValidationsAttributes.size());
    }


    @Test
    public void test_getPathFromRoot(){
        ExampleDataA root = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        root.referenceAttribute.set(exampleFactoryB);

        root.internal().finalise();
        Assertions.assertNotNull(root.internal().getRoot());

        List<FactoryBase<?,?>> pathTo = root.referenceAttribute.get().internal().getPathFromRoot();
        Assertions.assertEquals(1,pathTo.size());
        Assertions.assertEquals(root.getId(),pathTo.get(0).getId());
    }


    public static class ExampleObjectProperty extends FactoryBase<Void,ExampleObjectProperty> {
        public final StringAttribute stringAttribute= new StringAttribute().labelText("stringAttribute");
        public final ObjectValueAttribute<String> objectValueAttribute= new ObjectValueAttribute<String>().labelText("objectValueAttribute");
    }

    @Test
    public void test_copy_width_cycle(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryB.referenceAttribute.set(exampleFactoryA);

        ExampleDataA copy =  exampleFactoryA.utility().copy();

        //Assert no Stackoverflow
    }

    @Test
    public void test_copyOneLevelDeep(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        ExampleDataC exampleFactoryC = new ExampleDataC();
        exampleFactoryB.referenceAttributeC.set(exampleFactoryC);

        ExampleDataB factoryB = new ExampleDataB();
        factoryB.referenceAttributeC.set(new ExampleDataC());
        exampleFactoryA.referenceListAttribute.add(factoryB);

        Assertions.assertNotNull(exampleFactoryA.referenceAttribute.get());
        Assertions.assertNotNull(exampleFactoryA.referenceAttribute.get().referenceAttributeC.get());

        ExampleDataA copy =  exampleFactoryA.utility().copyOneLevelDeep();

        Assertions.assertNotEquals(copy,exampleFactoryA);
        Assertions.assertNotNull(copy.referenceAttribute.get());
        Assertions.assertNull(copy.referenceAttribute.get().referenceAttributeC.get());
        Assertions.assertNull(copy.referenceListAttribute.get(0).referenceAttributeC.get());
    }

    @Test
    public void test_copyOneLevelDeep_doubleref(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.referenceListAttribute.add(exampleFactoryB);

        ExampleDataA copy =  exampleFactoryA.utility().copyOneLevelDeep();

        Assertions.assertEquals(copy.referenceAttribute.get(),copy.referenceListAttribute.get().get(0));
    }

    private static class EmptyExampleDataA extends FactoryBase<Void,EmptyExampleDataA> {

    }

    @Test
    public void test_copy_consumer_root_empty_data(){
        EmptyExampleDataA emptyExampleDataA = new EmptyExampleDataA();
        EmptyExampleDataA copy =  emptyExampleDataA.internal().finalise();
        Assertions.assertEquals(copy,copy.internal().getRoot());
    }


    @Test
    public void test_copy(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryB.stringAttribute.set("dfssfdsfdsfd");

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());

        exampleFactoryA = exampleFactoryA.internal().finalise();

        ExampleDataA copy =  exampleFactoryA.internal().copy();

        SimpleObjectMapper mapper = ObjectMapperBuilder.build();
        System.out.println(mapper.writeValueAsString(exampleFactoryA));

        final ExampleDataA expectedData = mapper.copy(exampleFactoryA);
        String expected = mapper.writeValueAsString(expectedData);
        String actual = mapper.writeValueAsString(copy);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    public void test_zero_copy(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        exampleFactoryA.stringAttribute.set("dfssfdsfdsfd");
        exampleFactoryA.referenceAttribute.set(new ExampleDataB());
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());

        ExampleDataA copy =  exampleFactoryA.utility().copyZeroLevelDeep();


        Assertions.assertEquals("dfssfdsfdsfd", copy.stringAttribute.get());
        Assertions.assertEquals(null, copy.referenceAttribute.get());
        Assertions.assertTrue(copy.referenceListAttribute.get().isEmpty());
    }

    @Test
    public void test_copy_objectProperty(){
        ExampleObjectProperty exampleObjectProperty = new ExampleObjectProperty();
        final String test = "Test";
        exampleObjectProperty.objectValueAttribute.set(test);
        exampleObjectProperty=exampleObjectProperty.internal().finalise();
        ExampleObjectProperty copy = exampleObjectProperty.internal().copy();
        //that comparision ist correct (no equals)
        Assertions.assertTrue(copy.objectValueAttribute.get()==test);
    }


    @Test
    public void test_parent_navigation(){
        ExampleDataA dataA = new ExampleDataA();
        ExampleDataB dataB = new ExampleDataB();
        ExampleDataC dataC = new ExampleDataC();

        dataA.referenceAttribute.set(dataB);
        dataB.referenceAttributeC.set(dataC);

        dataA = dataA.internal().finalise();
        dataB = dataA.referenceAttribute.get();
        dataC = dataB.referenceAttributeC.get();

        Assertions.assertEquals(dataB,dataC.internal().getParents().iterator().next());
        Assertions.assertEquals(dataA,dataB.internal().getParents().iterator().next());

    }


    public static class ExampleWithId extends FactoryBase<Void,ExampleWithId> {
        public final StringAttribute id = new StringAttribute();
    }

    @Test
    public void test_json_and_id_attributename(){
        Assertions.assertThrows(IllegalStateException.class, () -> {
            ExampleWithId exampleWithId = new ExampleWithId();
            exampleWithId.id.set("blabla");
            exampleWithId.internal().finalise();
        });

//        System.out.println(ObjectMapperBuilder.build().writeValueAsString(exampleWithId));
//        ExampleWithId copy = ObjectMapperBuilder.build().copy(exampleWithId);
//        Assertions.assertNotEquals("blabla",copy.getId());
//        Assertions.assertEquals("blabla",copy.id.get());
    }


    public static class ExampleParentsA extends FactoryBase<Void,ExampleParentsA> {
        public final FactoryAttribute<Void,ExampleParentsB> exampleParentsB = new FactoryAttribute<>();
        public final FactoryAttribute<Void,ExampleParentsC> exampleParentsC = new FactoryAttribute<>();
    }

    public static class ExampleParentsB extends FactoryBase<Void,ExampleParentsA> {
        public final FactoryAttribute<Void,ExampleParentsC> exampleParentsC = new FactoryAttribute<>();
    }

    public static class ExampleParentsC extends FactoryBase<Void,ExampleParentsA> {
        public final StringAttribute any = new StringAttribute();
    }

    @Test
    public void test_multiple_parents(){
        ExampleParentsA exampleParentsA = new ExampleParentsA();
        ExampleParentsB exampleParentsB = new ExampleParentsB();
        exampleParentsA.exampleParentsB.set(exampleParentsB);

        ExampleParentsC exampleParentsC = new ExampleParentsC();
        exampleParentsB.exampleParentsC.set(exampleParentsC);
        exampleParentsA.exampleParentsC.set(exampleParentsC);

        //Assertions.assertEquals(2,exampleParentsC.internal().getParents().size());
        exampleParentsA = exampleParentsA.internal().finalise();
        Assertions.assertEquals(2,exampleParentsA.exampleParentsB.get().exampleParentsC.get().internal().getParents().size());
    }


    @Test
    public void test_parents_list_nested(){
        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.referenceAttributeC.set(new ExampleDataC());
        exampleDataA.referenceListAttribute.add(exampleDataB);

        exampleDataA = exampleDataA.internal().finalise();
        Assertions.assertEquals(1,exampleDataA.referenceListAttribute.get(0).internal().getParents().size());
        Assertions.assertEquals(1,exampleDataA.referenceListAttribute.get(0).referenceAttributeC.get().internal().getParents().size());
    }

    @Test
    public void test_addBackReferencese(){
        ExampleDataA example= new ExampleDataA();

        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.referenceAttributeC.set(new ExampleDataC());
        example.referenceListAttribute.add(exampleDataB);

        example.internal().finalise();

        Assertions.assertEquals(1,example.referenceListAttribute.get(0).internal().getParents().size());
        Assertions.assertEquals(1,example.referenceListAttribute.get(0).referenceAttributeC.get().internal().getParents().size());
    }

    @Test
    public void test_parents_list_nested_merge(){
        ExampleDataA current= new ExampleDataA();
        current = current.internal().finalise();

        ExampleDataA update = current.internal().copy();

        ExampleDataB exampleDataB = new ExampleDataB();
        exampleDataB.referenceAttributeC.set(new ExampleDataC());
        update.referenceListAttribute.add(exampleDataB);


        new DataMerger<>(current,current.utility().copy(),update).mergeIntoCurrent((p)->true);

        Assertions.assertEquals(1,current.referenceListAttribute.get(0).internal().getParents().size());
        Assertions.assertEquals(1,current.referenceListAttribute.get(0).referenceAttributeC.get().internal().getParents().size());


    }

    @Test
    public void test_json_null_attribute() throws IOException {
        ExampleDataA exampleDataA = new ExampleDataA();
//        exampleDataA.stringAttribute.set("adsa");
        exampleDataA.stringAttribute.set(null);
        ObjectMapper objectMapper = ObjectMapperBuilder.buildNewObjectMapper();
        String content = objectMapper.writeValueAsString(exampleDataA);
        ExampleDataA copy = objectMapper.readValue(content,ExampleDataA.class);

        System.out.println(content);
        Assertions.assertNotNull(copy.stringAttribute);
        Assertions.assertNotNull(copy.referenceListAttribute);
        Assertions.assertNotNull(copy.referenceAttribute);
    }

    @Test
    public void test_json_metadata() throws IOException {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.stringAttribute.set("adad");
        exampleDataA.referenceAttribute.set(new ExampleDataB());


        ObjectMapper objectMapper = ObjectMapperBuilder.buildNewObjectMapper();
        String content = objectMapper.writeValueAsString(exampleDataA);
        System.out.println(content);
        ExampleDataA copy = objectMapper.readValue(content,ExampleDataA.class);

        Assertions.assertEquals("ExampleA1",copy.stringAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assertions.assertEquals("adad",copy.stringAttribute.get());
        Assertions.assertEquals("ExampleA2",copy.referenceAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
        Assertions.assertEquals("ExampleA3",copy.referenceListAttribute.internal_getPreferredLabelText(Locale.ENGLISH));
    }

    @Test
    public void test_json_list() throws IOException {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.referenceListAttribute.add(new ExampleDataB());
        exampleDataA.referenceListAttribute.add(new ExampleDataB());

        ObjectMapper objectMapper = ObjectMapperBuilder.buildNewObjectMapper();
        String content = objectMapper.writeValueAsString(exampleDataA);
        System.out.println(content);
        ExampleDataA copy = objectMapper.readValue(content, ExampleDataA.class);

        Assertions.assertTrue(copy.referenceListAttribute.get(0) instanceof ExampleDataB);
        Assertions.assertEquals(2,copy.referenceListAttribute.size());
    }

    @Test
    public void test_json_list_with_ids() throws IOException {
        ExampleDataA exampleDataA = new ExampleDataA();
        ExampleDataB value = new ExampleDataB();
        value.referenceAttribute.set(new ExampleDataA());
        exampleDataA.referenceAttribute.set(value);
        exampleDataA.referenceListAttribute.add(value);
        exampleDataA.referenceListAttribute.add(new ExampleDataB());

        ObjectMapper objectMapper = ObjectMapperBuilder.buildNewObjectMapper();
        String content = objectMapper.writeValueAsString(exampleDataA);
        System.out.println(content);
        ExampleDataA copy = objectMapper.readValue(content,ExampleDataA.class);

        Assertions.assertEquals(copy.referenceAttribute.get(),copy.referenceListAttribute.get().get(0));
    }

    @Test
    public void test_unique_ids() {
        HashSet<UUID> ids=new HashSet<>();
        for (int i=0;i<1000000;i++){
            ExampleDataA exampleDataA = new ExampleDataA();
            UUID id = exampleDataA.getId();
            if (!ids.add(id)) {
                Assertions.fail("doubel id"+id+" count:"+ i);
            }
        }
    }

    @Test
    public void addBackReferences_cycle(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryB.referenceAttribute.set(exampleFactoryA);
        exampleFactoryA.referenceAttribute.set(exampleFactoryB);

        exampleFactoryA.internal().finalise();
    }

    @Test
    public void test_visitChildren(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        ExampleDataB exampleFactoryB = new ExampleDataB();
        exampleFactoryB.referenceAttribute.set(new ExampleDataA());

        exampleFactoryA.referenceAttribute.set(exampleFactoryB);
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());
        exampleFactoryA.referenceListAttribute.add(new ExampleDataB());

        exampleFactoryA.internal().finalise();
        Assertions.assertEquals(5,exampleFactoryA.internal().collectChildrenDeep().size());
    }

    @Test
    public void test_idEquals(){
        ExampleDataA exampleFactoryA = new ExampleDataA();
        exampleFactoryA.internal().finalise();
        Assertions.assertTrue(exampleFactoryA.idEquals(exampleFactoryA.utility().copy()));
        Assertions.assertFalse(exampleFactoryA.idEquals(new ExampleDataA()));
    }

    @Test
    public void test_copy_uuid(){
        FactoryBase<?,?> exampleFactoryA = new ExampleDataA();
        exampleFactoryA.getId();
        Assertions.assertTrue(exampleFactoryA.id instanceof UUID);

        Assertions.assertTrue(exampleFactoryA.utility().copy().id instanceof UUID);

        Assertions.assertTrue(exampleFactoryA.utility().copy().id instanceof UUID);
    }

    @Test
    public void test_copy_uuid_bugrecreate(){
        ExampleDataA currentModel = new ExampleDataA();
        currentModel.internal().finalise();

        ExampleDataA originalModel = currentModel.internal().copy();
        ExampleDataA newModel = currentModel.internal().copy();

        Assertions.assertTrue(currentModel.internal().copy().id instanceof UUID);
        Assertions.assertTrue(originalModel.internal().copy().id instanceof UUID);
        Assertions.assertTrue(newModel.internal().copy().id instanceof UUID);
    }

    @Test
    public void test_root_aftercopy(){
        ExampleDataA data = new ExampleDataA();
        data.internal().finalise();

        ExampleDataA copy = data.internal().copy();
        Assertions.assertEquals(copy,copy.internal().getRoot());

        Assertions.assertEquals(copy,getRoot(copy.referenceAttribute));
    }

    @Test
    public void test_root_aftercopy_nested(){
        ExampleDataA root = new ExampleDataA();
        root.referenceAttribute.set(new ExampleDataB());
        root.internal().finalise();

        Assertions.assertEquals(root,root.internal().getRoot());
        Assertions.assertEquals(root,getRoot(root.referenceAttribute.get().referenceAttribute));

        ExampleDataA copy = root.internal().copy();
        Assertions.assertEquals(copy,copy.internal().getRoot());
        Assertions.assertEquals(copy,getRoot(copy.referenceAttribute.get().referenceAttribute));
    }


    public static class ExampleRootSet extends FactoryBase<Void, ExampleRootSet> {
        public final FactoryAttribute<Void,ExampleRootSet> referenceAttribute = new FactoryAttribute<>();
    }

    @Test
    public void test_root_attribute_aftercopy_simple(){
        ExampleRootSet root = new ExampleRootSet();
        root.referenceAttribute.set(new ExampleRootSet());
        root.internal().finalise();

        Assertions.assertEquals(root,getRoot(root.referenceAttribute));

        ExampleRootSet copy = root.internal().copy();
        Assertions.assertEquals(copy,getRoot(copy.referenceAttribute));

        Assertions.assertEquals(root,getRoot(root.referenceAttribute));
    }


    private FactoryBase<?,?> getRoot(ReferenceBaseAttribute attribute){
        Field root = null;
        try {
            root = ReferenceBaseAttribute.class.getDeclaredField("root");
            root.setAccessible(true);
            return (FactoryBase<?,?>) root.get(attribute);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test_getPathFromRoot_param(){
        ExampleDataA data = new ExampleDataA();
        ExampleDataB exampleDataB = new ExampleDataB();
        data.referenceAttribute.set(exampleDataB);
        data.internal().finalise();

        Assertions.assertEquals(1,exampleDataB.internal().getPathFromRoot().size());
        Assertions.assertEquals(data,exampleDataB.internal().getPathFromRoot().get(0));
    }

    @Test
    public void test_collectChildFactoriesDeepFromNode(){
        ExampleDataA root = new ExampleDataA();
        ExampleDataB factoryB = new ExampleDataB();
        root.referenceAttribute.set(factoryB);
        factoryB.referenceAttributeC.set(new ExampleDataC());

        factoryB.internal().finalise();
        Assertions.assertEquals(2,factoryB.internal().collectChildrenDeep().size());
    }

    @Test
    public void test_copy_reflist_copied(){
        ExampleDataA original = new ExampleDataA();
        ExampleDataA copy = original.internal().copy();
        Assertions.assertFalse(original.referenceListAttribute==copy.referenceListAttribute);
    }

    @Test
    public void test_collectChildrenDeepFromNode_count(){
        ExampleDataA original = new ExampleDataA();
        original.internal().finalise();
        original.referenceListAttribute.add(new ExampleDataB());
        original.referenceListAttribute.add(new ExampleDataB());
        DataStorageMetadataDictionary dataStorageMetadataDictionary = original.internal().createDataStorageMetadataDictionaryFromRoot();
        Assertions.assertTrue(dataStorageMetadataDictionary.isSingleton(ExampleDataA.class.getName()));
        Assertions.assertFalse(dataStorageMetadataDictionary.isSingleton(ExampleDataB.class.getName()));
    }

}