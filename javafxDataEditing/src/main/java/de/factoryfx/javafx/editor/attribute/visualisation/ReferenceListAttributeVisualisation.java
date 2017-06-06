package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.attribute.ListAttributeEditorVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.datalistedit.DataListEditWidget;
import de.factoryfx.javafx.widget.table.TableControlWidget;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

public class ReferenceListAttributeVisualisation extends ListAttributeEditorVisualisation<Data> {

    private final UniformDesign uniformDesign;
    private final DataEditor dataEditor;
    private final DataListEditWidget<Data> dataListEditWidget;
    private final TableView<Data> tableView;

    public ReferenceListAttributeVisualisation(UniformDesign uniformDesign, DataEditor dataEditor, TableView<Data> tableView, DataListEditWidget<Data> dataListEditWidget) {
        this.uniformDesign = uniformDesign;
        this.dataEditor = dataEditor;
        this.dataListEditWidget = dataListEditWidget;
        this.tableView = tableView;
    }


    @Override
    public Node createContent(List<Data> attributeValue) {
        tableView.setItems((ObservableList<Data>) attributeValue);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Data, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().internal().getDisplayText()));
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem()!=null) {
                    dataEditor.edit(tableView.getSelectionModel().getSelectedItem());
                }
            }
        });

        TableControlWidget<Data> tableControlWidget = new TableControlWidget<>(tableView,uniformDesign);
        Node tableControlWidgetContent = tableControlWidget.createContent();
        HBox.setHgrow(tableControlWidgetContent, Priority.ALWAYS);
        HBox.setMargin(tableControlWidgetContent, new Insets(0,1,0,0));

        HBox buttons = (HBox)dataListEditWidget.createContent();
        buttons.getChildren().add(tableControlWidgetContent);

        VBox vbox = new VBox();
        VBox.setVgrow(tableView,Priority.ALWAYS);
        vbox.getChildren().add(tableView);
        vbox.getChildren().add(buttons);
        return vbox;
    }
}
