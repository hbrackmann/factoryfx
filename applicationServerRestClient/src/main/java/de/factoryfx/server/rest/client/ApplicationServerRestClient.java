package de.factoryfx.server.rest.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.base.Strings;
import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.datastorage.FactoryAndStorageMetadata;
import de.factoryfx.factory.datastorage.StoredFactoryMetadata;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.client.filter.EncodingFilter;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.message.DeflateEncoder;
import org.glassfish.jersey.message.GZipEncoder;

public class ApplicationServerRestClient<V,T extends FactoryBase<?,V>> {

    private final Client client;
    private final URI baseURI;
    private final Class<T> factoryRootClass;
    private final String httpAuthenticationUser;
    private final String httpAuthenticationPassword;

    public ApplicationServerRestClient(String host, int port, String path,boolean ssl, Class<T> factoryRootClass, String httpAuthenticationUser, String httpAuthenticationPassword) {
        this(buildURI(host, port, ssl, path), factoryRootClass,httpAuthenticationUser,httpAuthenticationPassword);
    }

    public ApplicationServerRestClient(URI baseURI, Class<T> factoryRootClass, String httpAuthenticationUser, String httpAuthenticationPassword) {
        this.baseURI = baseURI;
        this.factoryRootClass = factoryRootClass;
        this.httpAuthenticationUser=httpAuthenticationUser;
        this.httpAuthenticationPassword=httpAuthenticationPassword;
        this.client = createClient();
    }

    public MergeDiffInfo updateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        return post("updateCurrentFactory", update, MergeDiffInfo.class);
    }

    public MergeDiffInfo simulateUpdateCurrentFactory(FactoryAndStorageMetadata<T> update) {
        return post("simulateUpdateCurrentFactory", update, MergeDiffInfo.class);
    }

    @SuppressWarnings("unchecked")
    public FactoryAndStorageMetadata<T> getCurrentFactory() {
        FactoryAndStorageMetadata<T> currentFactory = get("currentFactory", FactoryAndStorageMetadata.class);
        currentFactory.root.internal().reconstructMetadataDeepRoot();
        return currentFactory;
    }


    @SuppressWarnings("unchecked")
    public FactoryAndStorageMetadata<T> prepareNewFactory() {
        FactoryAndStorageMetadata<T> currentFactory = get("prepareNewFactory", FactoryAndStorageMetadata.class);
        currentFactory.root.internal().reconstructMetadataDeepRoot();
        return currentFactory;
    }


    public T getHistoryFactory(String id) {
        return get("historyFactory", factoryRootClass).internal().reconstructMetadataDeepRoot();
    }

    static final Class<? extends ArrayList<StoredFactoryMetadata>> collectionOfStoredFactoryMetadataClass = new ArrayList<StoredFactoryMetadata>() {}.getClass();
    public Collection<StoredFactoryMetadata> getHistoryFactoryList() {
        return get("historyFactoryList", collectionOfStoredFactoryMetadataClass);
    }

    public void start() {
        get("start");
    }

    public void stop() {
        get("stop");
    }


    @SuppressWarnings("unchecked")
    public V query(V visitor) {
        return post("query",visitor,(Class<? extends V>)visitor.getClass());
    }

    private <R> R post(String subPath, Object entity, Class<R> returnType) {
        Response response = createRequest(subPath).post(Entity.json(entity));
        checkResponseStatus(response);
        return response.readEntity(returnType);
    }

    private <R> R get(String subPath, Class<R> returnType) {
        Response response = createRequest(subPath).get();
        checkResponseStatus(response);
        return response.readEntity(returnType);
    }

    private void checkResponseStatus(Response response) {
        if (response.getStatus() != 200)
            throw new RuntimeException("Received http status code "+response.getStatus()+"\n"+response.readEntity(String.class));
    }

    private Invocation.Builder createRequest(String subPath) {
        return client.target(baseURI.resolve(subPath)).request().accept(MediaType.APPLICATION_JSON_TYPE);
    }

    private Object get(String subPath) {
        return createRequest(subPath).get().getEntity();
    }


    private static URI buildURI(String host, int port, boolean ssl, String path)  {
        try {
            return new URI((ssl?"https":"http")+"://"+host+":"+port+"/"+path+"/");
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("bad host name",e);
        }
    }


    public Client createClient() {
        ClientConfig cc = new ClientConfig().register(new JacksonFeature());
        Client client = ClientBuilder.newBuilder().withConfig(cc).build();
        client.register(GZipEncoder.class);
        client.register(EncodingFilter.class);
        client.register(DeflateEncoder.class);
        if (!Strings.isNullOrEmpty(httpAuthenticationUser) && !Strings.isNullOrEmpty(httpAuthenticationPassword) ){
            client.register(HttpAuthenticationFeature.basic(httpAuthenticationUser, httpAuthenticationPassword));
        }
        JacksonJaxbJsonProvider provider = new JacksonJaxbJsonProvider();
        provider.setMapper(ObjectMapperBuilder.buildNew().getObjectMapper());
        client.register(provider);
        return client;
    }


}
