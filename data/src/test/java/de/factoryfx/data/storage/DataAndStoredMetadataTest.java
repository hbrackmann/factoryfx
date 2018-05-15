package de.factoryfx.data.storage;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testfactories.ExampleDataA;
import org.junit.Test;

public class DataAndStoredMetadataTest {
    @Test
    public void test_json(){
        DataAndStoredMetadata<ExampleDataA,Void> test = new DataAndStoredMetadata<>(new ExampleDataA(),new StoredDataMetadata<>("","","","",0,null));
        ObjectMapperBuilder.build().copy(test);
    }
}