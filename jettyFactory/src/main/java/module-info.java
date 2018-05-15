module de.factoryfx.jettyFactory {
    requires java.ws.rs;
    requires slf4j.api;
    requires javax.servlet.api;
    requires de.factoryfx.factory;
    requires jetty.util;
    requires de.factoryfx.data;
    requires com.google.common;
    requires com.fasterxml.jackson.databind;
    requires java.logging;
    requires jetty.server;
    requires jersey.common;
    requires com.fasterxml.jackson.jaxrs.json;
    requires jetty.servlet;
    requires jersey.server;
    requires jersey.container.servlet.core;
    requires jackson.annotations;

    exports de.factoryfx.jetty;
    exports de.factoryfx.jetty.ssl;
}