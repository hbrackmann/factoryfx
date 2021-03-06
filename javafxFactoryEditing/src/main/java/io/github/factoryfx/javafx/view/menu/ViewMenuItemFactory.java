package io.github.factoryfx.javafx.view.menu;

import io.github.factoryfx.factory.SimpleFactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryAttribute;
import io.github.factoryfx.javafx.RichClientRoot;
import io.github.factoryfx.javafx.view.View;
import io.github.factoryfx.javafx.view.ViewDescription;
import io.github.factoryfx.javafx.view.ViewDescriptionFactory;
import io.github.factoryfx.javafx.view.ViewFactory;
import javafx.scene.control.MenuItem;

public class ViewMenuItemFactory extends SimpleFactoryBase<MenuItem,RichClientRoot> {

    public final FactoryAttribute<ViewDescription,ViewDescriptionFactory> viewDescription = new FactoryAttribute<>();
    public final FactoryAttribute<View,ViewFactory> view = new FactoryAttribute<>();

    @Override
    protected MenuItem createImpl() {
        MenuItem menuItem = new MenuItem();
        ViewDescription viewDescription = this.viewDescription.instance();
        viewDescription.describeMenuItem(menuItem);

        View createdView = view.instance();

        menuItem.setOnAction(event -> {
            createdView.show();
        });
        return menuItem;
    }

}