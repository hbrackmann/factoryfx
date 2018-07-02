package de.factoryfx.javafx.data.widget.select;

import de.factoryfx.data.Data;
import de.factoryfx.javafx.css.CssUtil;
import de.factoryfx.javafx.data.util.DataObservableDisplayText;
import de.factoryfx.javafx.data.util.UniformDesign;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class SelectDataDialog {
    public final List<Data> dataList;
    private final UniformDesign uniformDesign;

    public SelectDataDialog(List<Data> dataList, UniformDesign uniformDesign) {
        this.dataList = dataList;
        this.uniformDesign = uniformDesign;
    }

    public void show(Window owner, Consumer<Data> success){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle("Select");
        dialog.setHeaderText("Select");

//        ButtonType loginButtonType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        final BorderPane pane = new BorderPane();
        TableView<Data> table = new TableView<>();
        table.getItems().setAll(dataList);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        TableColumn<Data, String> column = new TableColumn<>();
        column.setCellValueFactory(param -> new DataObservableDisplayText(param.getValue()).get());
        table.getColumns().add(column);
        pane.setCenter(table);
        pane.setPrefWidth(1000);
        pane.setPrefHeight(750);
        dialog.getDialogPane().setContent(pane);
        dialog.setResizable(true);

        CssUtil.addToNode(dialog.getDialogPane());
        dialog.getDialogPane().lookupButton(ButtonType.OK).disableProperty().bind(table.getSelectionModel().selectedItemProperty().isNull());

        final Optional<ButtonType> dialogResult = dialog.showAndWait();
        if (dialogResult.get() == ButtonType.OK && table.getSelectionModel().getSelectedItem()!=null){
            success.accept(table.getSelectionModel().getSelectedItem());
        }
    }

}