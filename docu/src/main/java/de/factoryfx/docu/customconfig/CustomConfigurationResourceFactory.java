package de.factoryfx.docu.customconfig;

import de.factoryfx.factory.SimpleFactoryBase;

public class CustomConfigurationResourceFactory extends SimpleFactoryBase<CustomConfigurationResource, ServerFactory> {

    @Override
    public CustomConfigurationResource createImpl() {
        return new CustomConfigurationResource(this.utilityFactory().getMicroservice());
    }

}
