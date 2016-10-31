package de.factoryfx.javafx.widget.dataview;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.editor.data.ExampleData1;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.util.UniformDesignFactory;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DataViewWidgetTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        ExampleData1 exampleData1 = new ExampleData1();
        exampleData1.stringAttribute.set("abc");

        UniformDesign uniformDesign = new UniformDesignFactory<>().instance();
        DataEditor dataEditor = new DataEditor(new AttributeEditorFactory(uniformDesign, exampleData1), uniformDesign);
        dataEditor.edit(exampleData1);

        ObservableList<Data> dataList = FXCollections.observableArrayList();
        dataList.addAll(exampleData1);
        dataList.addAll(new ExampleData1());
        dataList.addAll(new ExampleData1());
        dataList.addAll(new ExampleData1());

        DataViewWidget dataViewWidget = new DataViewWidget(new DataView(dataList),dataEditor);

        BorderPane root = new BorderPane();
        root.getStylesheets().add(getClass().getResource("/de/factoryfx/javafx/css/app.css").toExternalForm());
        root.setCenter(dataViewWidget.createContent());
        primaryStage.setScene(new Scene(root, 1200, 800));

        primaryStage.show();

    }

    public static void main(String[] args) {
//        System.setProperty("glass.win.minHiDPI","1.5");
//        System.setProperty("glass.win.forceIntegerRenderScale", "false");
//        System.setProperty("glass.win.minHiDPI","1.0");
        Application.launch();
    }
}