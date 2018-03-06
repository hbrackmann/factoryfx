package de.factoryfx.server.rest;

import ch.qos.logback.classic.Level;
import de.factoryfx.data.attribute.types.EncryptedString;
import de.factoryfx.data.attribute.types.EncryptedStringAttribute;
import de.factoryfx.data.storage.StoredDataMetadata;
import de.factoryfx.data.storage.inmemory.InMemoryDataStorage;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.FactoryManager;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.exception.RethrowingFactoryExceptionHandler;
import de.factoryfx.server.ApplicationServer;
import de.factoryfx.server.rest.client.ApplicationServerRestClient;
import de.factoryfx.server.rest.client.ApplicationServerRestClientFactory;
import de.factoryfx.server.rest.client.RestClientFactory;
import de.factoryfx.server.rest.server.HttpServerConnectorFactory;
import de.factoryfx.server.rest.server.JettyServer;
import de.factoryfx.server.rest.server.JettyServerFactory;
import de.factoryfx.user.persistent.PersistentUserManagementFactory;
import de.factoryfx.user.persistent.UserFactory;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class ApplicationServerRestTest {

    public static class TestJettyServer extends JettyServerFactory<Void>{
        public final FactoryReferenceAttribute<ApplicationServerResource<Void, String, RootTestclazz,Void>, ApplicationServerResourceFactory<Void, String, RootTestclazz,Void>> resource = new FactoryReferenceAttribute<>();
        @Override
        protected List<Object> getResourcesInstances() {
            return Arrays.asList(resource.instance());
        }
    }

    @Test
    public void integration_test() {
        String key  = new EncryptedStringAttribute().createKey();
        UserFactory.passwordKey=key;

        ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) org.slf4j.LoggerFactory.getLogger(ch.qos.logback.classic.Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.INFO);

//        ObjectMapperBuilder.build().getObjectMapper().registerSubtypes(UserManagementFactory.class);


        TestJettyServer jettyServer = new TestJettyServer();
        final HttpServerConnectorFactory<Void> httpServerConnectorFactory = new HttpServerConnectorFactory<>();
        httpServerConnectorFactory.port.set(34579);
        httpServerConnectorFactory.host.set("localhost");
        jettyServer.connectors.add(httpServerConnectorFactory);
        final ApplicationServerResourceFactory<Void, String, RootTestclazz, Void> applicationServerResource = new ApplicationServerResourceFactory<>();
        jettyServer.resource.set(applicationServerResource);
        final PersistentUserManagementFactory<Void> userManagement = new PersistentUserManagementFactory<>();
        final UserFactory<Void> user = new UserFactory<>();
        user.name.set("user123");
        user.password.set(new EncryptedString("hash123", key));
        user.locale.set(Locale.GERMAN);
        userManagement.users.add(user);
        applicationServerResource.userManagement.set(userManagement);

        RootTestclazz rootTestclazz = new RootTestclazz();
        rootTestclazz.jettyServer.set(jettyServer);
        rootTestclazz = rootTestclazz.utility().prepareUsableCopy();
        ApplicationServer<Void, String, RootTestclazz, Void> applicationServer = new ApplicationServer<>(new FactoryManager<>(new RethrowingFactoryExceptionHandler<>()), new InMemoryDataStorage<>(rootTestclazz));
        Thread serverThread = new Thread(() -> {
            applicationServer.start();
        });
        serverThread.start();

        try {

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }


            ApplicationServerRestClientFactory<Void, FactoryBase<Void, Void>, Void, RootTestclazz> applicationServerRestClientFactory = new ApplicationServerRestClientFactory<>();
            final RestClientFactory<Void> restClient = new RestClientFactory<>();
            restClient.port.set(34579);
            restClient.host.set("localhost");
            restClient.path.set("adminui");
            applicationServerRestClientFactory.restClient.set(restClient);
            applicationServerRestClientFactory.user.set("user123");
            applicationServerRestClientFactory.passwordHash.set("hash123");
            applicationServerRestClientFactory.factoryRootClass.set(RootTestclazz.class);

            ApplicationServerRestClient<Void, RootTestclazz> applicationServerRestClient = applicationServerRestClientFactory.internalFactory().instance();
            applicationServerRestClient.prepareNewFactory();


            final ArrayList<StoredDataMetadata> historyFactoryList = new ArrayList<>(applicationServerRestClient.getHistoryFactoryList());
            applicationServerRestClient.getHistoryFactory(historyFactoryList.get(0).id);

            Assert.assertEquals(Locale.GERMAN, applicationServerRestClient.getLocale());
        } finally {
            applicationServer.stop();
        }
    }


    public static class RootTestclazz extends SimpleFactoryBase<String,Void> {
        public final FactoryReferenceAttribute<JettyServer,JettyServerFactory<Void>> jettyServer = new FactoryReferenceAttribute<>();

        @Override
        public String createImpl() {
            jettyServer.instance();
            return "";
        }
    }
}
