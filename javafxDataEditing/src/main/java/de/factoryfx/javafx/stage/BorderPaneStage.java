package de.factoryfx.javafx.stage;

import java.util.List;

import de.factoryfx.javafx.view.container.ViewsDisplayWidget;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class BorderPaneStage {
    public final Stage stage;

    public BorderPaneStage(Stage stage, List<Menu> menus, ViewsDisplayWidget instance, int width, int height) {
        this.stage = stage;

        BorderPane root = new BorderPane();
        root.setCenter(instance.createContent());
        MenuBar menuBar = new MenuBar();
        menuBar.getMenus().addAll(menus);
        root.setTop(menuBar);
        stage.setScene(new Scene(root,width,height));


    }


}
