package io.github.factoryfx.factory.testfactories.poly;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.testfactories.ExampleFactoryA;

public class OutPrinterFactory extends SimpleFactoryBase<Printer,ExampleFactoryA> {
    @Override
    protected Printer createImpl() {
        return new OutPrinter();
    }

}
