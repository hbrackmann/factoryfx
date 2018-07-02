module de.factoryfx.javafxDistributionServer {
    requires de.factoryfx.data;
    requires de.factoryfx.factory;
    requires jetty.server;
    requires jetty.util;
    requires java.ws.rs;
    requires com.google.common;

    exports de.factoryfx.javafx.distribution.launcher.rest;
    exports de.factoryfx.javafx.distribution.server.rest;
}