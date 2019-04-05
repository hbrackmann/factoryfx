package io.github.factoryfx.javafx.factory.widget.factory.dataview;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.javafx.factory.editor.data.DataEditor;
import io.github.factoryfx.javafx.factory.util.UniformDesign;
import io.github.factoryfx.javafx.factory.widget.dataview.DataViewWidget;
import io.github.factoryfx.javafx.factory.RichClientRoot;
import io.github.factoryfx.javafx.factory.editor.DataEditorFactory;
import io.github.factoryfx.javafx.factory.util.UniformDesignFactory;

public class DataViewWidgetFactory<RS extends FactoryBase<?,RS>,L,F extends FactoryBase<L,RS>> extends SimpleFactoryBase<DataViewWidget<RS,L,F>, RichClientRoot> {

    public final FactoryReferenceAttribute<RichClientRoot, UniformDesign, UniformDesignFactory> uniformDesign = new FactoryReferenceAttribute<>();
    public final FactoryReferenceAttribute<RichClientRoot, DataEditor, DataEditorFactory> dataEditorFactory = new FactoryReferenceAttribute<>();

    @Override
    public DataViewWidget<RS,L,F> createImpl() {
        return new DataViewWidget<>(dataEditorFactory.instance(),uniformDesign.instance());
    }
}