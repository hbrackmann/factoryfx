package de.factoryfx.data.storage.migration;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.ScheduledDataMetadata;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class MigrationManagerTest {

    @Test
    public void read_read(){
        MigrationManager<ExampleDataA,Void> manager =  new MigrationManager<>(ExampleDataA.class, List.of(), GeneralStorageMetadataBuilder.build(), new DataMigrationManager(),ObjectMapperBuilder.build());

        ExampleDataA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()), createExampleMetadata());
        Assert.assertNotNull(result);

    }

    private ScheduledDataMetadata<Void> createExampleMetadata(GeneralStorageMetadata generalStorageMetadata, DataStorageMetadataDictionary dataStorageMetadataDictionary) {
        return new ScheduledDataMetadata<>(null,"","","","",null, generalStorageMetadata,dataStorageMetadataDictionary,null);
    }

    private ScheduledDataMetadata<Void> createExampleMetadata(GeneralStorageMetadata generalStorageMetadata) {
        ExampleDataA exampleDataA = new ExampleDataA();
        exampleDataA.internal().addBackReferences();
        return createExampleMetadata(generalStorageMetadata, exampleDataA.internal().createDataStorageMetadataDictionaryFromRoot());
    }
    private ScheduledDataMetadata<Void> createExampleMetadata() {
        return createExampleMetadata(GeneralStorageMetadataBuilder.build());
    }

    @Test
    public void read_read_general_migration_format_(){
        List<GeneralMigration> migrations = new ArrayList<>();
        migrations.add(new GeneralMigration() {
            @Override
            public boolean canMigrate(GeneralStorageMetadata generalStorageMetadata) {
                return generalStorageMetadata.match(new GeneralStorageMetadata(1,0));
            }

            @Override
            public String migrate(String data) {
                return data;//nothing
            }

            @Override
            public GeneralStorageMetadata migrationResultStorageFormat() {
                return new GeneralStorageMetadata(2,0);
            }
        });

        GeneralStorageMetadata generalStorageMetadata = new GeneralStorageMetadata(2,0);
        MigrationManager<ExampleDataA,Void> manager = new MigrationManager<>(ExampleDataA.class, migrations, generalStorageMetadata, new DataMigrationManager(),ObjectMapperBuilder.build());
        ExampleDataA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()), createExampleMetadata(new GeneralStorageMetadata(1,0)));
        Assert.assertNotNull(result);
    }

    @Test
    public void read_read_general_migration__nested(){
        List<GeneralMigration> migrations = new ArrayList<>();
        migrations.add(new GeneralMigration() {
            @Override
            public boolean canMigrate(GeneralStorageMetadata generalStorageMetadata) {
                return generalStorageMetadata.match(new GeneralStorageMetadata(1,0));
            }

            @Override
            public String migrate(String data) {
                return data;//nothing
            }

            @Override
            public GeneralStorageMetadata migrationResultStorageFormat() {
                return new GeneralStorageMetadata(2,0);
            }
        });
        migrations.add(new GeneralMigration() {
            @Override
            public boolean canMigrate(GeneralStorageMetadata generalStorageMetadata) {
                return generalStorageMetadata.match(new GeneralStorageMetadata(2,0));
            }

            @Override
            public String migrate(String data) {
                return data;//nothing
            }

            @Override
            public GeneralStorageMetadata migrationResultStorageFormat() {
                return new GeneralStorageMetadata(3,0);
            }
        });


        GeneralStorageMetadata generalStorageMetadata = new GeneralStorageMetadata(3,0);
        MigrationManager<ExampleDataA,Void> manager = new MigrationManager<>(ExampleDataA.class, migrations, generalStorageMetadata, new DataMigrationManager(),ObjectMapperBuilder.build());
        ExampleDataA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()), createExampleMetadata(new GeneralStorageMetadata(1,0)));
        Assert.assertNotNull(result);

    }

    @Test(expected = IllegalStateException.class)
    public void read_no_migration_found(){
        List<GeneralMigration> migrations = new ArrayList<>();

        GeneralStorageMetadata generalStorageMetadata = new GeneralStorageMetadata(2,0);
        MigrationManager<ExampleDataA,Void> manager = new MigrationManager<>(ExampleDataA.class, migrations, generalStorageMetadata, new DataMigrationManager(),ObjectMapperBuilder.build());
        ExampleDataA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()), createExampleMetadata(new GeneralStorageMetadata(1,0)));
        Assert.assertNotNull(result);

    }

    @Test(expected = IllegalStateException.class)
    public void read_no_migration_found_chained(){
        List<GeneralMigration> migrations = new ArrayList<>();
        migrations.add(new GeneralMigration() {
            @Override
            public boolean canMigrate(GeneralStorageMetadata generalStorageMetadata) {
                return generalStorageMetadata.match(new GeneralStorageMetadata(1,0));
            }

            @Override
            public String migrate(String data) {
                return data;//nothing
            }

            @Override
            public GeneralStorageMetadata migrationResultStorageFormat() {
                return new GeneralStorageMetadata(2,0);
            }
        });
        //missing 2 to 3
//        attributeRenames.add(new GeneralStorageFormatMigration() {
//            @Override
//            public boolean canMigrate(GeneralStorageFormat generalStorageFormat) {
//                return generalStorageFormat.match(new GeneralStorageFormat(2,0));
//            }
//
//            @Override
//            public String migrate(String data) {
//                return data;//nothing
//            }
//
//            @Override
//            public GeneralStorageFormat migrationResultStorageFormat() {
//                return new GeneralStorageFormat(3,0);
//            }
//        });


        GeneralStorageMetadata generalStorageMetadata = new GeneralStorageMetadata(3,0);
        MigrationManager<ExampleDataA,Void> manager = new MigrationManager<>(ExampleDataA.class, migrations, generalStorageMetadata, new DataMigrationManager(),ObjectMapperBuilder.build());
        ExampleDataA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()), createExampleMetadata(new GeneralStorageMetadata(1,0)));
        Assert.assertNotNull(result);

    }


    @Test
    public void read_read_migration_nested_and_dependent(){
        List<GeneralMigration> migrations = new ArrayList<>();
        migrations.add(new GeneralMigration() {
            @Override
            public boolean canMigrate(GeneralStorageMetadata generalStorageMetadata) {
                return generalStorageMetadata.match(new GeneralStorageMetadata(1,0));
            }

            @Override
            public String migrate(String data) {
                return "1"+data;
            }

            @Override
            public GeneralStorageMetadata migrationResultStorageFormat() {
                return new GeneralStorageMetadata(2,0);
            }
        });
        migrations.add(new GeneralMigration() {
            @Override
            public boolean canMigrate(GeneralStorageMetadata generalStorageMetadata) {
                return generalStorageMetadata.match(new GeneralStorageMetadata(2,0));
            }

            @Override
            public String migrate(String data) {
                if (!data.startsWith("1")){//hack to simulate migration depending on previous migration
                    throw new IllegalStateException();
                }
                return data.substring(1);
            }

            @Override
            public GeneralStorageMetadata migrationResultStorageFormat() {
                return new GeneralStorageMetadata(3,0);
            }
        });


        GeneralStorageMetadata generalStorageMetadata = new GeneralStorageMetadata(3,0);
        MigrationManager<ExampleDataA,Void> manager = new MigrationManager<>(ExampleDataA.class, migrations, generalStorageMetadata, new DataMigrationManager(),ObjectMapperBuilder.build());
        ExampleDataA result = manager.read(ObjectMapperBuilder.build().writeValueAsString(new ExampleDataA()), createExampleMetadata(new GeneralStorageMetadata(1,0)));
        Assert.assertNotNull(result);
    }

    @Test
    public void test_renameAttribute() {

        String oldDictionary =   "{\n" +
                                 "  \"dataStorageMetadataList\" : [ {\n" +
                                 "    \"attributes\" : [ {\n" +
                                 "      \"variableName\" : \"wrongName\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.types.StringAttribute\"\n" +
                                 "    }, {\n" +
                                 "      \"variableName\" : \"referenceAttribute\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceAttribute\"\n" +
                                 "    }, {\n" +
                                 "      \"variableName\" : \"referenceListAttribute\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceListAttribute\"\n" +
                                 "    } ],\n" +
                                 "    \"className\" : \"de.factoryfx.data.merge.testdata.ExampleDataA\"\n" +
                                 "  }, {\n" +
                                 "    \"attributes\" : [ {\n" +
                                 "      \"variableName\" : \"stringAttribute\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.types.StringAttribute\"\n" +
                                 "    }, {\n" +
                                 "      \"variableName\" : \"referenceAttribute\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceAttribute\"\n" +
                                 "    }, {\n" +
                                 "      \"variableName\" : \"referenceAttributeC\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceAttribute\"\n" +
                                 "    } ],\n" +
                                 "    \"className\" : \"de.factoryfx.data.merge.testdata.ExampleDataB\"\n" +
                                 "  }, {\n" +
                                 "    \"attributes\" : [ {\n" +
                                 "      \"variableName\" : \"stringAttribute\",\n" +
                                 "      \"attributeClassName\" : \"de.factoryfx.data.attribute.types.StringAttribute\"\n" +
                                 "    } ],\n" +
                                 "    \"className\" : \"de.factoryfx.data.merge.testdata.ExampleDataC\"\n" +
                                 "  } ]\n" +
                                 "}";

        String input =
                "{\n" +
                "  \"@class\" : \"de.factoryfx.data.merge.testdata.ExampleDataA\",\n" +
                "  \"id\" : \"d2412cc8-c759-58f5-93f8-ef4e195114b5\",\n" +
                "  \"wrongName\" : {\n" + //<======
                "    \"v\" : \"123\"\n" +
                "  },\n" +
                "  \"referenceAttribute\" : { },\n" +
                "  \"referenceListAttribute\" : [ ]\n" +
                "}";

        DataMigrationManager migrations = new DataMigrationManager();
        migrations.renameAttribute(ExampleDataA.class, "wrongName",d->d.stringAttribute);

        GeneralStorageMetadata generalStorageMetadata = new GeneralStorageMetadata(1, 0);
        MigrationManager<ExampleDataA,Void> manager = new MigrationManager<>(ExampleDataA.class, List.of(), generalStorageMetadata, migrations, ObjectMapperBuilder.build());
        ExampleDataA result = manager.read(input, createExampleMetadata(generalStorageMetadata,ObjectMapperBuilder.build().readValue(oldDictionary,DataStorageMetadataDictionary.class)));
        Assert.assertEquals("123",result.stringAttribute.get());
    }

    @Test
    public void read_renameClass() {

        String oldDictionary =   "{\n" +
                "  \"dataStorageMetadataList\" : [ {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceListAttribute\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceListAttribute\"\n" +
                "    } ],\n" +
                "    \"className\" : \"de.factoryfx.data.merge.testdata.WrongNameExampleDataA\"\n" +//<======
                "  }, {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.types.StringAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttribute\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceAttribute\"\n" +
                "    }, {\n" +
                "      \"variableName\" : \"referenceAttributeC\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.DataReferenceAttribute\"\n" +
                "    } ],\n" +
                "    \"className\" : \"de.factoryfx.data.merge.testdata.ExampleDataB\"\n" +
                "  }, {\n" +
                "    \"attributes\" : [ {\n" +
                "      \"variableName\" : \"stringAttribute\",\n" +
                "      \"attributeClassName\" : \"de.factoryfx.data.attribute.types.StringAttribute\"\n" +
                "    } ],\n" +
                "    \"className\" : \"de.factoryfx.data.merge.testdata.ExampleDataC\"\n" +
                "  } ]\n" +
                "}";

        String input =
                "{\n" +
                "  \"@class\" : \"de.factoryfx.data.merge.testdata.WrongNameExampleDataA\",\n" +//<======
                "  \"id\" : \"d2412cc8-c759-58f5-93f8-ef4e195114b5\",\n" +
                "  \"stringAttribute\" : {\n" +
                "    \"v\" : \"123\"\n" +
                "  },\n" +
                "  \"referenceAttribute\" : { },\n" +
                "  \"referenceListAttribute\" : [ ]\n" +
                "}";

        DataMigrationManager migrations = new DataMigrationManager();
        migrations.renameClass("de.factoryfx.data.merge.testdata.WrongNameExampleDataA",ExampleDataA.class);

        GeneralStorageMetadata generalStorageMetadata = new GeneralStorageMetadata(1, 0);
        MigrationManager<ExampleDataA,Void> manager = new MigrationManager<>(ExampleDataA.class, List.of(), generalStorageMetadata, migrations,ObjectMapperBuilder.build());
        ExampleDataA result = manager.read(input, createExampleMetadata(generalStorageMetadata,ObjectMapperBuilder.build().readValue(oldDictionary,DataStorageMetadataDictionary.class)));
        Assert.assertEquals("123",result.stringAttribute.get());
    }

}