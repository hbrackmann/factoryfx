package de.factoryfx.example.main;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.example.client.RichClientBuilder;
import de.factoryfx.example.server.ServerBuilder;
import de.factoryfx.example.server.ServerRootFactory;
import de.factoryfx.example.server.shop.OrderCollector;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.builder.FactoryTreeBuilder;
import de.factoryfx.factory.exception.LoggingFactoryExceptionHandler;
import de.factoryfx.factory.exception.ResettingHandler;
import de.factoryfx.javafx.factory.RichClientRoot;
import de.factoryfx.server.Microservice;
import de.factoryfx.server.MicroserviceBuilder;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.controlsfx.dialog.ExceptionDialog;
import org.eclipse.jetty.server.Server;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

public class ExampleMain extends Application {
    //Vm-options:
//--add-exports=javafx.base/com.sun.javafx.runtime=controlsfx
//--add-exports=javafx.graphics/com.sun.javafx.css=controlsfx
//--add-exports=javafx.base/com.sun.javafx.event=controlsfx
//--add-exports=javafx.graphics/com.sun.javafx.scene.traversal=controlsfx
//--add-exports=javafx.graphics/com.sun.javafx.scene=controlsfx
//--add-exports=javafx.controls/com.sun.javafx.scene.control=controlsfx
//--add-exports=javafx.base/com.sun.javafx.collections=controlsfx
    @Override
    public void start(Stage primaryStage){

        FactoryTreeBuilder<ServerRootFactory> serverBuilder = new ServerBuilder().builder();
        ServerRootFactory shopFactory = serverBuilder.buildTree();

        Microservice<OrderCollector, Server, ServerRootFactory, Void> shopService = new Microservice<>(new FactoryManager<>(new LoggingFactoryExceptionHandler<>(new ResettingHandler<OrderCollector, Server, ServerRootFactory>())), new InMemoryDataStorage<>(shopFactory));
        shopService.start();

        RichClientRoot richClientFactory = RichClientBuilder.createFactoryBuilder(8089,primaryStage, "", "", Locale.ENGLISH,serverBuilder).buildTree();
        MicroserviceBuilder.buildInMemoryMicroservice(richClientFactory).start();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        Platform.runLater(() -> {
            try {
                Desktop.getDesktop().browse(new URI("http://localhost:8089/shop"));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void main(String[] args) {
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            Platform.runLater(()-> new ExceptionDialog(e).showAndWait());
        });

        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

        Application.launch();
    }






}
