package de.factoryfx.javafx.editor.attribute.visualisation;

import java.util.List;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.table.TableControlWidget;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class ViewListReferenceAttributeVisualisation implements AttributeEditorVisualisation<List<Data>> {

    private final DataEditor dataEditor;
    private final UniformDesign uniformDesign;

    public ViewListReferenceAttributeVisualisation(DataEditor dataEditor, UniformDesign uniformDesign) {
        this.dataEditor = dataEditor;
        this.uniformDesign = uniformDesign;
    }

    @Override
    public Node createContent(SimpleObjectProperty<List<Data>> boundTo) {
        VBox detailView = createDetailView(boundTo);
        return uniformDesign.createExpandableListWrapper(boundTo, detailView);
    }


    private VBox createDetailView(SimpleObjectProperty<List<Data>> boundTo) {
        TableView<Data> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Data, String> test = new TableColumn<>("Data");
        test.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getDisplayText()));
        tableView.getColumns().add(test);
        tableView.getStyleClass().add("hidden-tableview-headers");
        ObservableList<Data> items = FXCollections.observableArrayList();
        tableView.setItems(items);

        tableView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                if (mouseEvent.getClickCount() == 2 && tableView.getSelectionModel().getSelectedItem()!=null) {
                    dataEditor.edit(tableView.getSelectionModel().getSelectedItem());
                }
            }
        });
        ;
        InvalidationListener listener = observable -> {
            if (boundTo.get() == null) {
                items.clear();
            } else {
                items.setAll(boundTo.get());
            }
        };
        boundTo.addListener(listener);
        listener.invalidated(null);


        TableControlWidget<Data> tableControlWidget = new TableControlWidget<>(tableView,uniformDesign);
        Node tableControlWidgetContent = tableControlWidget.createContent();
        HBox.setHgrow(tableControlWidgetContent, Priority.ALWAYS);
        HBox.setMargin(tableControlWidgetContent, new Insets(0,1,0,0));

        VBox vbox = new VBox();
        vbox.getChildren().add(tableView);
        vbox.getChildren().add(tableControlWidgetContent);
        return vbox;
    }
}
