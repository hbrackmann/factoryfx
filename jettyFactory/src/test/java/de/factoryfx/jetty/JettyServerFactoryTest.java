package de.factoryfx.jetty;

import de.factoryfx.data.jackson.ObjectMapperBuilder;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.JettyServerFactory;
import org.junit.Test;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.List;

public class JettyServerFactoryTest {
    @Path("/Resource1")
    public static class Resource1{
        @GET()
        public Response get(){
            return Response.ok().build();
        }
    }

    public static class TestWebserverFactory extends JettyServerFactory<Void,TestWebserverFactory> {
        public final FactoryReferenceAttribute<Resource1,FactoryBase<Resource1,Void,TestWebserverFactory>> resource = new FactoryReferenceAttribute<>();
            @Override
        protected List<Object> getResourcesInstances() {
            return Arrays.asList(resource.instance());
        }
    }

    @Test
    public void test_json(){
        TestWebserverFactory factoryBases = new TestWebserverFactory();
        factoryBases.resource.set(new FactoryBase<>());

        ObjectMapperBuilder.build().copy(factoryBases);
    }

}