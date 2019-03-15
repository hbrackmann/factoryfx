package de.factoryfx.jetty;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

import java.util.logging.Handler;

public abstract class HandlerFactory<V,R extends FactoryBase<?,R>> extends SimpleFactoryBase<Handler,R> {
}
