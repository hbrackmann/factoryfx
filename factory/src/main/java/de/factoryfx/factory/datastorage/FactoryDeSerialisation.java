package de.factoryfx.factory.datastorage;

import de.factoryfx.factory.FactoryBase;

public interface FactoryDeSerialisation<T extends FactoryBase<?,?>> {
    boolean canRead(String data, int dataModelVersion);
    T read(String data);
    StoredFactoryMetadata readStorageMetadata(String data);
}
