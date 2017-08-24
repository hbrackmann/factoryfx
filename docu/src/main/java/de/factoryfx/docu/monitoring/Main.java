package de.factoryfx.docu.monitoring;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.datastorage.inmemory.InMemoryFactoryStorage;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.rest.server.HttpServerConnectorFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws Exception {
        RootFactory rootFactory = new RootFactory();
        InstrumentedJettyServerFactory jettyServer = new InstrumentedJettyServerFactory();
        final HttpServerConnectorFactory<ServerVisitor> httpServerConnectorFactory = new HttpServerConnectorFactory<>();
        httpServerConnectorFactory.port.set(34576);
        httpServerConnectorFactory.host.set("localhost");
        jettyServer.connectors.add(httpServerConnectorFactory);
        rootFactory.server.set(jettyServer);
        jettyServer.resources.add(new SimpleResourceFactory());

        ApplicationServer<ServerVisitor,Root,RootFactory> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()),new InMemoryFactoryStorage<>(rootFactory));
        applicationServer.start();

        //execute some random request as example
        getHTML("http://localhost:34576");

        //for the sake of simplicity the query ist called in the same vm.
        //you could also use the applicationServerRestServer / applicationServerRestClient from a different process if ServerVisitor is json serializable
        ServerVisitor serverVisitor = new ServerVisitor();
        applicationServer.query(serverVisitor);
        System.out.println(serverVisitor.jettyReport);

        //report shows 1 get request
        //...
        //monitoring example.get-requests
        //     count = 1
    }

    public static String getHTML(String urlToRead) throws Exception {
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        try (InputStream inputStream= conn.getInputStream()){
            return CharStreams.toString(new InputStreamReader(inputStream, Charsets.UTF_8));
        }
    }
}