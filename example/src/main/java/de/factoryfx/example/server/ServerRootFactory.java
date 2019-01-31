package de.factoryfx.example.server;

import de.factoryfx.example.server.shop.*;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import org.eclipse.jetty.server.Server;

public class ServerRootFactory extends ServerBaseFactory<Server> {

    public final FactoryReferenceAttribute<Server, ShopJettyServerFactory> httpServer =
            new FactoryReferenceAttribute<>(ShopJettyServerFactory.class)
                    .labelText("HTTP Servers")
                    .userNotCreatable()
                    .userNotSelectable()
                    .userReadOnly();

    public Server createImpl() {
        return httpServer.instance();
    }

    public ServerRootFactory(){
        this.config().setDisplayTextProvider(()->"Server");
    }
}
