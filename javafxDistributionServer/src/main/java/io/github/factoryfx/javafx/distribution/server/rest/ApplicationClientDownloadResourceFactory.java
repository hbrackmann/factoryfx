package io.github.factoryfx.javafx.distribution.server.rest;

import java.io.File;

import io.github.factoryfx.factory.attribute.types.StringAttribute;
import io.github.factoryfx.factory.FactoryBase;

public class ApplicationClientDownloadResourceFactory<R extends FactoryBase<?,R>> extends FactoryBase<ApplicationClientDownloadResource,R> {
    public final StringAttribute guiZipFile = new StringAttribute().de("Datei für UI").en("File containing UI");

    public ApplicationClientDownloadResourceFactory() {
        config().setDisplayTextProvider(() -> "DownloadResource:"+guiZipFile.get());
        configLifeCycle().setCreator(() -> new ApplicationClientDownloadResource(new File(guiZipFile.get())));
    }

}
