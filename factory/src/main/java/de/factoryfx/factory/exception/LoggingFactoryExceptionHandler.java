package de.factoryfx.factory.exception;

import com.sun.net.httpserver.HttpServer;
import de.factoryfx.factory.FactoryBase;
import org.slf4j.LoggerFactory;

public class LoggingFactoryExceptionHandler<V> implements FactoryExceptionHandler<V>{

    private final FactoryExceptionHandler<V> delegate;

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HttpServer.class);

    public LoggingFactoryExceptionHandler(FactoryExceptionHandler<V> delegate) {
        this.delegate = delegate;
    }

    @Override
    public void createOrRecreateException(Exception e, FactoryBase<?,V> factory, ExceptionResponseAction exceptionResponse){
        log(e,factory);
        delegate.createOrRecreateException(e,factory,exceptionResponse);
    }
    @Override
    public void startException(Exception e, FactoryBase<?,V> factory, ExceptionResponseAction exceptionResponse) {
        log(e,factory);
        delegate.startException(e,factory,exceptionResponse);
    }
    @Override
    public void destroyException(Exception e, FactoryBase<?,V> factory, ExceptionResponseAction exceptionResponse) {
        log(e,factory);
        delegate.destroyException(e,factory,exceptionResponse);
    }

    private void log(Exception e, FactoryBase<?,V> factory) {
        logger.error(factory.internalFactory().debugInfo(), e);
    }
}
