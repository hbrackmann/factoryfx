package io.github.factoryfx.factory.typescript.generator.testserver;

import io.github.factoryfx.dom.rest.FilesystemStaticFileAccessFactory;
import io.github.factoryfx.dom.rest.MicroserviceDomResourceFactory;
import io.github.factoryfx.factory.FactoryTreeBuilderBasedAttributeSetup;
import io.github.factoryfx.factory.builder.FactoryTreeBuilder;
import io.github.factoryfx.factory.builder.Scope;
import io.github.factoryfx.jetty.JettyServerBuilder;
import io.github.factoryfx.jetty.JettyServerFactory;
import io.github.factoryfx.microservice.rest.MicroserviceResourceFactory;
import org.eclipse.jetty.server.Server;

import java.io.File;

public class TestServerBuilder {

    @SuppressWarnings("unchecked")
    public FactoryTreeBuilder< Server, TestServerFactory,Void> create() {
        FactoryTreeBuilder< Server, TestServerFactory,Void> builder = new FactoryTreeBuilder<>(TestServerFactory.class,(ctx)->{
            TestServerFactory testServerFactory = new TestServerFactory();
            testServerFactory.stringAttribute.set("1233");
            testServerFactory.server.set(ctx.get(JettyServerFactory.class));
            return testServerFactory;
        });
        builder.addFactory(JettyServerFactory.class, Scope.SINGLETON, ctx-> new JettyServerBuilder<TestServerFactory>()
                .withHost("localhost").withPort(8005)
                .withResource(ctx.get(MicroserviceDomResourceFactory.class))
                .build()
        );


        builder.addFactory(MicroserviceDomResourceFactory.class, Scope.SINGLETON, ctx->{
            MicroserviceDomResourceFactory microserviceDomResourceFactory = new MicroserviceDomResourceFactory();
            FilesystemStaticFileAccessFactory filesystemStaticFileAccessFactory = new FilesystemStaticFileAccessFactory();
            filesystemStaticFileAccessFactory.basePath.set(new File("./src/main/resources/js/").getAbsolutePath()+"/");
            microserviceDomResourceFactory.staticFileAccess.set(filesystemStaticFileAccessFactory);
            microserviceDomResourceFactory.factoryTreeBuilderBasedAttributeSetup.set(new FactoryTreeBuilderBasedAttributeSetup<>(builder));
            return microserviceDomResourceFactory;
        });

        return builder;
    }

}