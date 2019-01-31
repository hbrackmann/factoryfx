package de.factoryfx.data.storage.migration.metadata;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.testdata.ExampleDataA;
import org.junit.Assert;
import org.junit.Test;


public class DataStorageMetadataDictionaryTest {

    @Test
    public void test_json(){
        ObjectMapperBuilder.build().copy(new DataStorageMetadataDictionary(ExampleDataA.class));
        System.out.println( ObjectMapperBuilder.build().writeValueAsString(new DataStorageMetadataDictionary(ExampleDataA.class)));
    }

    @Test
    public void test_json_stable(){
        Assert.assertEquals(ObjectMapperBuilder.build().writeValueAsString(new DataStorageMetadataDictionary(ExampleDataA.class)),ObjectMapperBuilder.build().writeValueAsString(new DataStorageMetadataDictionary(ExampleDataA.class)));
    }

    @Test
    public void test_init(){
        Assert.assertEquals(3,new DataStorageMetadataDictionary(ExampleDataA.class).dataStorageMetadataList.size());
    }
}