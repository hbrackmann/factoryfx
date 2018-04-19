package de.factoryfx.javafx.widget;

import de.factoryfx.factory.FactoryBase;
import de.factoryfx.factory.SimpleFactoryBase;

public abstract class WidgetFactory<V,R extends FactoryBase<?,V,R>> extends SimpleFactoryBase<Widget,V,R> {

    @Override
    public Widget createImpl() {
        return createWidget();
    }
    protected abstract Widget createWidget() ;
}
