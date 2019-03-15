package de.factoryfx.javafx.distribution.launcher.rest;

import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;

public class DistributionClientDownloadResourceFactory<V,R extends FactoryBase<?,R>> extends FactoryBase<DistributionClientDownloadResource,R> {
    public final StringAttribute distributionClientZipPath = new StringAttribute().labelText("Distribution client zip path on the server");

    public DistributionClientDownloadResourceFactory() {
        config().setDisplayTextProvider(() -> "Distribution client download path:"+distributionClientZipPath.get());
        configLifeCycle().setCreator(() -> new DistributionClientDownloadResource(distributionClientZipPath.get()));
    }

}
