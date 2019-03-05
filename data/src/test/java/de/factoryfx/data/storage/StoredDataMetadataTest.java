package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import de.factoryfx.data.storage.migration.DataMigrationManager;
import de.factoryfx.data.storage.migration.GeneralStorageMetadata;
import de.factoryfx.data.storage.migration.GeneralStorageMetadataBuilder;
import de.factoryfx.data.storage.migration.MigrationManager;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

public class StoredDataMetadataTest {

    private static class SummaryDummy{
        public long diffCounter=1;
    }


    @Test
    public void test_json(){
        LocalDateTime now = LocalDateTime.now();
        StoredDataMetadata<SummaryDummy> value=new StoredDataMetadata<>(now, "", "", "", "sdfgstrg", new SummaryDummy(),new GeneralStorageMetadata(1,2),null);
        final StoredDataMetadata<SummaryDummy> copy = ObjectMapperBuilder.build().copy(value);

        System.out.println(
                ObjectMapperBuilder.build().writeValueAsString(value)
        );
        Assertions.assertEquals(now,copy.creationTime);
        Assertions.assertEquals(1,copy.changeSummary.diffCounter);
        Assertions.assertTrue(copy.generalStorageMetadata.match(new GeneralStorageMetadata(1, 2)));

    }

    @Test
    public void test_compatible_to_old_format(){
        String old =
                "{\n" +
                "  \"creationTime\" : \"2019-01-14T16:54:02.695571\",\n" +
                "  \"baseVersionId\" : \"sdfgstrg\",\n" + "  \"dataModelVersion\" : 0,\n" +
                "  \"changeSummary\" : {\n" +
                "    \"@class\" : \"de.factoryfx.data.storage.StoredDataMetadataTest$SummaryDummy\",\n" +
                "    \"diffCounter\" : 1\n" +
                "  }\n" +
                "}";
        MigrationManager<ExampleDataA,SummaryDummy> manager = new MigrationManager<>(ExampleDataA.class, List.of(), GeneralStorageMetadataBuilder.build(), new DataMigrationManager<>((root1, oldDataStorageMetadataDictionary) -> { },ExampleDataA.class), ObjectMapperBuilder.build());
        final StoredDataMetadata<SummaryDummy> oldParsed = manager.readStoredFactoryMetadata(old);


    }


}