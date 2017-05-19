package de.factoryfx.docu.lifecycle;

import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;

public class Main {

    public static void main(String[] args) {
        RootFactory root = new RootFactory();

        ApplicationServer<Void,Root,RootFactory> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()),new InMemoryFactoryStorage<>(root));
        applicationServer.start();
        applicationServer.stop();

    }
}