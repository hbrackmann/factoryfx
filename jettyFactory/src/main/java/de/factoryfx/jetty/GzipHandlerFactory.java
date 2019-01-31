package de.factoryfx.jetty;

import de.factoryfx.data.attribute.primitive.BooleanAttribute;
import de.factoryfx.data.attribute.primitive.IntegerAttribute;
import de.factoryfx.data.attribute.types.EnumListAttribute;
import de.factoryfx.data.attribute.types.StringListAttribute;
import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.PolymorphicFactoryBase;
import de.factoryfx.factory.atrribute.FactoryPolymorphicReferenceAttribute;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.gzip.GzipHandler;

import javax.servlet.DispatcherType;

public class GzipHandlerFactory<V,R extends FactoryBase<?,V,R>> extends PolymorphicFactoryBase<Handler,V,R> {
    public final FactoryPolymorphicReferenceAttribute<Handler> handler = new FactoryPolymorphicReferenceAttribute<>(Handler.class).labelText("Handler");
    public final IntegerAttribute minGzipSize = new IntegerAttribute().labelText("minGzipSize");
    public final IntegerAttribute compressionLevel = new IntegerAttribute().labelText("compressionLevel");
    public final IntegerAttribute deflaterPoolCapacity = new IntegerAttribute().labelText("deflaterPoolCapacity");
    public final EnumListAttribute<DispatcherType> dispatcherTypes = new EnumListAttribute<>(DispatcherType.class).labelText("dispatcherTypes");
    public final StringListAttribute excludedAgentPatterns = new StringListAttribute().labelText("excludedAgentPatterns");
    public final StringListAttribute excludedMethods = new StringListAttribute().labelText("excludedMethods");
    public final StringListAttribute excludedMimeTypes = new StringListAttribute().labelText("excludedMimeTypes");
    public final StringListAttribute excludedPaths = new StringListAttribute().labelText("excludedPaths");
    public final StringListAttribute includedAgentPatterns = new StringListAttribute().labelText("includedAgentPatterns");
    public final StringListAttribute includedMethods = new StringListAttribute().labelText("includedMethods");
    public final StringListAttribute includedMimeTypes = new StringListAttribute().labelText("includedMimeTypes");
    public final StringListAttribute includedPaths = new StringListAttribute().labelText("includedPaths");
    public final IntegerAttribute inflateBufferSize = new IntegerAttribute().labelText("inflateBufferSize");
    public final BooleanAttribute syncFlush = new BooleanAttribute().labelText("syncFlush");


    @Override
    public Handler createImpl() {
        GzipHandler gzipHandler = new GzipHandler();
        gzipHandler.setMinGzipSize(minGzipSize.get());
        gzipHandler.setCompressionLevel(compressionLevel.get());
        gzipHandler.setDeflaterPoolCapacity(deflaterPoolCapacity.get());
        gzipHandler.setDispatcherTypes(dispatcherTypes.get().toArray(new DispatcherType[0]));
        gzipHandler.setExcludedAgentPatterns(excludedAgentPatterns.get().toArray(new String[0]));
        gzipHandler.setExcludedMethods(excludedMethods.get().toArray(new String[0]));
        gzipHandler.setExcludedMimeTypes(excludedMimeTypes.get().toArray(new String[0]));
        gzipHandler.setExcludedPaths(excludedPaths.get().toArray(new String[0]));
        gzipHandler.setIncludedAgentPatterns(includedAgentPatterns.get().toArray(new String[0]));
        gzipHandler.setIncludedMethods(includedMethods.get().toArray(new String[0]));
        gzipHandler.setIncludedMimeTypes(includedMimeTypes.get().toArray(new String[0]));
        gzipHandler.setIncludedPaths(includedPaths.get().toArray(new String[0]));
        gzipHandler.setInflateBufferSize(inflateBufferSize.get());
        gzipHandler.setSyncFlush(syncFlush.get());

        gzipHandler.setHandler(handler.instance());
        return gzipHandler;
    }
}
