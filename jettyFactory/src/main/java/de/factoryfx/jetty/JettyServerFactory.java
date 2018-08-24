package de.factoryfx.jetty;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.factory.atrribute.FactoryReferenceListAttribute;

/**
 *   Unusual inheritance api to support type-safe navigation.
 *  (alternative would be a factorylist but there is no common interface for resources)
 *
 *  usage example.
 *
 *  <pre>{@code
 *      public static class TestWebserverFactory extends JettyServerFactory<Void,RootFactory>{
 *          public final FactoryReferenceAttribute<Resource1,Resource1FactoryBase> resource = new FactoryReferenceAttribute<>(Resource1FactoryBase.class);
 *          Override
 *          protected List<Object> getResourcesInstances() {
 *              return List.of(resource.instance());
 *          }
 *      }
 *  }</pre>
 */
public abstract class JettyServerFactory<V,R extends FactoryBase<?,V,R>> extends FactoryBase<JettyServer,V,R> {

    /** jersey resource class with Annotations*/
//    public final FactoryReferenceListAttribute<Object,FactoryBase<?,V>> resources = new FactoryReferenceListAttribute<Object,FactoryBase<?,V>>().setupUnsafe(FactoryBase.class).labelText("resource");
    @SuppressWarnings("unchecked")
    public final FactoryReferenceListAttribute<HttpServerConnectorCreator,HttpServerConnectorFactory<V,R>> connectors =
            FactoryReferenceListAttribute.create( new FactoryReferenceListAttribute<>(HttpServerConnectorFactory.class).labelText("Connectors").userNotSelectable());
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<ObjectMapper,FactoryBase<ObjectMapper,V,R>> objectMapper =
            FactoryReferenceAttribute.create( new FactoryReferenceAttribute<>(FactoryBase.class).nullable().en("fdsf"));
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<org.glassfish.jersey.logging.LoggingFeature,FactoryBase<org.glassfish.jersey.logging.LoggingFeature,V,R>> restLogging =
            FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(FactoryBase.class).userReadOnly().nullable().labelText("REST logging"));


    public JettyServerFactory(){
        configLiveCycle().setCreator(this::createJetty);
        configLiveCycle().setReCreator(currentLiveObject->currentLiveObject.recreate(connectors.instances(), getResourcesInstancesNullRemoved()));

        configLiveCycle().setStarter(JettyServer::start);
        configLiveCycle().setDestroyer(JettyServer::stop);

        config().setDisplayTextProvider(() -> "Microservice REST server");
    }

    //api for customizing JettyServer creation
    protected JettyServer createJetty() {
        return new JettyServer(connectors.instances(), getResourcesInstancesNullRemoved(), objectMapper.instance(),restLogging.instance());
    }

    protected List<Object> getResourcesInstancesNullRemoved(){
        return getResourcesInstances().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * @return jersey resource class with Annotation
     * */
    @JsonIgnore
    protected abstract List<Object> getResourcesInstances();
}
