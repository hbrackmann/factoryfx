package de.factoryfx.jetty;

import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.StringAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.jetty.ssl.SslContextFactoryFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class HttpServerConnectorFactory<R extends FactoryBase<?,R>> extends SimpleFactoryBase<HttpServerConnector,R> {
    public final StringAttribute host = new StringAttribute().de("host").en("host");
    public final IntegerAttribute port = new IntegerAttribute().de("port").en("port");
    @SuppressWarnings("unchecked")
    public final FactoryReferenceAttribute<SslContextFactory,SslContextFactoryFactory<R>> ssl =
            FactoryReferenceAttribute.create(new FactoryReferenceAttribute<>(SslContextFactoryFactory.class).de("ssl").en("ssl").nullable());


    @Override
    public HttpServerConnector createImpl() {
        return new HttpServerConnector(host.get(),port.get(),ssl.instance());
    }
    public HttpServerConnectorFactory(){
        config().setDisplayTextProvider(() -> {
            String protocol="http";
            if (ssl.get()!=null){
                protocol="https";
            }
            return protocol+"://"+host.get()+":"+port.get();
        });
    }
}
