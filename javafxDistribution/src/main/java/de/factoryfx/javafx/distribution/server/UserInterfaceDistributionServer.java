package de.factoryfx.javafx.distribution.server;

import java.util.logging.LogManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import de.factoryfx.javafx.distribution.server.rest.DownloadResource;
import de.factoryfx.adminui.javafx.server.AllExceptionMapper;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.NetworkTrafficServerConnector;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserInterfaceDistributionServer {

    private static final Logger logger = LoggerFactory.getLogger(UserInterfaceDistributionServer.class);

    private final DownloadResource downloadResource;
    private final String host;
    private final int port;
    private final org.eclipse.jetty.server.Server server;
    private final NetworkTrafficServerConnector connector;

    public UserInterfaceDistributionServer(String host, int port, DownloadResource downloadResource) {
        this.downloadResource = downloadResource;
        this.host = host;
        this.port = port;

        server=new org.eclipse.jetty.server.Server();

        connector = new NetworkTrafficServerConnector(server);
        connector.setPort(port);
        connector.setHost(host);

        server.setConnectors(new Connector[]{connector});


        ServletContextHandler contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        contextHandler.addServlet( new ServletHolder(new ServletContainer(jerseySetup(downloadResource))), "/download/*");

        ErrorHandler errorHandler = new ErrorHandler();
        errorHandler.setShowStacks(true);
        contextHandler.setErrorHandler(errorHandler);


        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[] {contextHandler, new DefaultHandler() });
        server.setHandler(handlers);
    }

    private ResourceConfig jerseySetup(Object resource) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.register(resource);
        resourceConfig.register(new AllExceptionMapper());

        ObjectMapper mapper = new ObjectMapper();

        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(mapper);
        resourceConfig.register(provider);

        LoggingFilter loggingFilter = new LoggingFilter(java.util.logging.Logger.getLogger(LoggingFilter.class.getName()),true);
        resourceConfig.registerInstances(loggingFilter);
        LogManager.getLogManager().reset();
        return resourceConfig;
    }

    public void start() throws Error {
        try {
            server.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void close() {
        try {
            server.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}