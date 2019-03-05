package de.factoryfx.data.storage.migration.datamigration;

import de.factoryfx.data.Data;
import de.factoryfx.data.storage.migration.metadata.DataStorageMetadataDictionary;

import java.util.List;
import java.util.function.BiConsumer;

/** restore attribute content from one data class to a new one, both  are singletons**/
public class SingletonDataRestore<R extends Data,V>  {

    private final String previousAttributeName;
    private String singletonPreviousDataClass;

    private final BiConsumer<R,V> setter;
    private final Class<V> valueClass;

    public SingletonDataRestore(String singletonPreviousDataClass, String previousAttributeName, Class<V> valueClass, BiConsumer<R,V> setter) {
        this.previousAttributeName = previousAttributeName;
        this.singletonPreviousDataClass=singletonPreviousDataClass;
        this.setter=setter;
        this.valueClass=valueClass;
    }

    public boolean canMigrate(DataStorageMetadataDictionary previousDataStorageMetadataDictionary, DataStorageMetadataDictionary currentDataStorageMetadataDictionary){
        return currentDataStorageMetadataDictionary.isRemovedAttribute(singletonPreviousDataClass, previousAttributeName) &&
               previousDataStorageMetadataDictionary.isSingleton(singletonPreviousDataClass) &&
               previousDataStorageMetadataDictionary.containsClass(singletonPreviousDataClass) &&
               previousDataStorageMetadataDictionary.containsAttribute(singletonPreviousDataClass,previousAttributeName);
    }

    public void migrate(List<DataJsonNode> dataJsonNodes, R root) {
        DataJsonNode previousData = dataJsonNodes.stream().filter(dataJsonNode -> dataJsonNode.match(singletonPreviousDataClass)).findFirst().get();
        V attributeValue = previousData.getAttributeValue(previousAttributeName, valueClass);

        setter.accept(root,attributeValue);
    }
}
