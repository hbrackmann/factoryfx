package de.factoryfx.factory.exception;

import de.factoryfx.factory.FactoryBase;

public interface FactoryExceptionHandler {

    void updateException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction exceptionResponse);
    void startException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction exceptionResponse);
    void destroyException(Exception e, FactoryBase<?,?,?> factory, ExceptionResponseAction exceptionResponse);

}
