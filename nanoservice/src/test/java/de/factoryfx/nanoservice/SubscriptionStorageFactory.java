package de.factoryfx.nanoservice;

import de.factoryfx.factory.SimpleFactoryBase;

public class SubscriptionStorageFactory extends SimpleFactoryBase<SubscriptionStorage,RootFactory> {

    @Override
    public SubscriptionStorage createImpl() {
        return new SubscriptionStorage();
    }
}
