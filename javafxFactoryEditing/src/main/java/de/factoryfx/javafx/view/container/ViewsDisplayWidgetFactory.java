package de.factoryfx.javafx.view.container;

import de.factoryfx.data.attribute.AttributeMetadata;
import de.factoryfx.factory.SimpleFactoryBase;
import de.factoryfx.factory.atrribute.FactoryReferenceAttribute;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import javafx.scene.control.TabPane;

public class ViewsDisplayWidgetFactory<V> extends SimpleFactoryBase<ViewsDisplayWidget,V> {
    public final FactoryReferenceAttribute<UniformDesign,UniformDesignFactory<V>> uniformDesign = new FactoryReferenceAttribute<>(new AttributeMetadata().de("view").en("view"),UniformDesignFactory.class);

    @Override
    public ViewsDisplayWidget createImpl() {
        return new ViewsDisplayWidget(new TabPane(),uniformDesign.instance());
    }
}