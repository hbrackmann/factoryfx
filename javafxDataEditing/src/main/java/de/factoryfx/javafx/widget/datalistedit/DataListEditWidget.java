package de.factoryfx.javafx.widget.datalistedit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.ReferenceListAttribute;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.editor.data.DataEditor;
import de.factoryfx.javafx.util.DataChoiceDialog;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Window;
import org.controlsfx.glyphfont.FontAwesome;

public class DataListEditWidget<T extends Data> implements Widget {

    private LanguageText editText= new LanguageText().en("edit").de("Editieren");
    private LanguageText selectText= new LanguageText().en("select").de("Auswählen");
    private LanguageText addText= new LanguageText().en("add").de("Hinzufügen");
    private LanguageText deleteText= new LanguageText().en("delete").de("Löschen");
    private LanguageText copyText= new LanguageText().en("copy").de("Kopieren");

    private LanguageText deleteConfirmationTitle= new LanguageText().en("Delte").de("Löschen");
    private LanguageText deleteConfirmationHeader= new LanguageText().en("delete item").de("Eintrag löschen");
    private LanguageText deleteConfirmationContent= new LanguageText().en("delete item?").de("Soll der Eintrag gelöscht werden?");


    private final UniformDesign uniformDesign;
    private final Runnable emptyAdder;
    private final Supplier<Collection<? extends Data>> possibleValuesProvider;
    private final boolean isUserEditable;
    private final boolean isUserSelectable;
    private final List<T> list;
    private final TableView<T> tableView;
    private final DataEditor dataEditor;
    private final BooleanBinding multipleItemsSelected;
    private final boolean isUserCreateable;
    private final BiConsumer<T,List<T>> deleter;

    public DataListEditWidget(List<T> list, TableView<T> tableView, DataEditor dataEditor, UniformDesign uniformDesign, Runnable emptyAdder, Supplier<Collection<? extends Data>> possibleValuesProvider, BiConsumer<T,List<T>> deleter , boolean isUserEditable, boolean isUserSelectable, boolean isUserCreateable) {
        this.uniformDesign = uniformDesign;
        this.emptyAdder = emptyAdder;
        this.possibleValuesProvider = possibleValuesProvider;
        this.isUserEditable = isUserEditable;
        this.isUserSelectable = isUserSelectable;
        this.list = list;
        this.tableView = tableView;
        this.dataEditor = dataEditor;
        multipleItemsSelected = Bindings.createBooleanBinding(() -> tableView.getSelectionModel().getSelectedItems().size() > 1, tableView.getSelectionModel().getSelectedItems());
        this.isUserCreateable = isUserCreateable;
        this.deleter=deleter;
    }

    public DataListEditWidget(List<T> list, TableView<T> tableView, DataEditor dataEditor, UniformDesign uniformDesign, ReferenceListAttribute<T> referenceListAttribute) {
        this(list, tableView, dataEditor, uniformDesign,
                referenceListAttribute::internal_addNewFactory, referenceListAttribute::internal_possibleValues, (t, ts) -> referenceListAttribute.internal_deleteFactory(t),
                referenceListAttribute.internal_isUserEditable(), referenceListAttribute.internal_isUserSelectable(), referenceListAttribute.internal_isUserCreatable());
    }


    @Override
    @SuppressWarnings("unchecked")
    public Node createContent() {
        Button showButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.PENCIL));
        showButton.setOnAction(event -> dataEditor.edit(tableView.getSelectionModel().getSelectedItem()));
        showButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected));

        Button selectButton = new Button("", uniformDesign.createIcon(FontAwesome.Glyph.SEARCH_PLUS));
        selectButton.setOnAction(event -> {
            Optional<Data> toAdd = new DataChoiceDialog().show(possibleValuesProvider.get(),selectButton.getScene().getWindow(),uniformDesign);
            toAdd.ifPresent(data -> list.add((T) data));
        });
        selectButton.setDisable(!isUserEditable || !isUserSelectable);

        Button adderButton = new Button();
        uniformDesign.addIcon(adderButton,FontAwesome.Glyph.PLUS);
        adderButton.setOnAction(event -> {
            emptyAdder.run();
            dataEditor.edit(list.get(list.size()-1));
        });
        adderButton.setDisable(!isUserEditable || !isUserCreateable);

        Button deleteButton = new Button();
        uniformDesign.addDangerIcon(deleteButton,FontAwesome.Glyph.TIMES);
        deleteButton.setOnAction(event -> deleteSelected(deleteButton.getScene().getWindow()));
        deleteButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(new SimpleBooleanProperty(!isUserEditable)));

        Button moveUpButton = new Button();
        uniformDesign.addIcon(moveUpButton,FontAwesome.Glyph.ANGLE_UP);
        moveUpButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected));
        moveUpButton.setOnAction(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex -1>=0){
                Collections.swap(list, selectedIndex, selectedIndex -1);
                tableView.getSelectionModel().select(selectedIndex -1);
            }
        });
        Button moveDownButton = new Button();
        uniformDesign.addIcon(moveDownButton,FontAwesome.Glyph.ANGLE_DOWN);
        moveDownButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected));
        moveDownButton.setOnAction(event -> {
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex+1<list.size()){
                Collections.swap(list, selectedIndex, selectedIndex +1);
                tableView.getSelectionModel().select(selectedIndex +1);
            }
        });
        Button sortButton = new Button();
        uniformDesign.addIcon(sortButton,FontAwesome.Glyph.SORT_ALPHA_ASC);
        sortButton.setOnAction(event -> {
            list.sort((d1,d2)->{
                String s1 = Optional.ofNullable(d1.internal().getDisplayText()).orElse("");
                String s2 = Optional.ofNullable(d2.internal().getDisplayText()).orElse("");
                return s1.compareTo(s2);
            });
        });

        Button copyButton = new Button();
        uniformDesign.addIcon(copyButton,FontAwesome.Glyph.COPY);
        copyButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull().or(multipleItemsSelected));
        copyButton.setOnAction(event -> {
            list.add(tableView.getSelectionModel().getSelectedItem().utility().semanticCopy());
        });

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER_LEFT);
        buttons.setSpacing(3);
        buttons.getChildren().add(showButton);
        buttons.getChildren().add(selectButton);
        buttons.getChildren().add(adderButton);
        buttons.getChildren().add(copyButton);
        buttons.getChildren().add(deleteButton);
        buttons.getChildren().add(moveUpButton);
        buttons.getChildren().add(moveDownButton);
        buttons.getChildren().add(sortButton);

        showButton.setTooltip(new Tooltip(uniformDesign.getText(editText)));
        selectButton.setTooltip(new Tooltip(uniformDesign.getText(selectText)));
        adderButton.setTooltip(new Tooltip(uniformDesign.getText(addText)));
        deleteButton.setTooltip(new Tooltip(uniformDesign.getText(deleteText)));
        copyButton.setTooltip(new Tooltip(uniformDesign.getText(copyText)));

        HBox.setMargin(moveUpButton,new Insets(0,0,0,9));
        HBox.setMargin(moveDownButton,new Insets(0,9,0,0));


        tableView.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode()== KeyCode.DELETE){
                deleteSelected(tableView.getScene().getWindow());
            }
        });
        return buttons;
    }

    private void deleteSelected(Window owner) {
        boolean reallyDelete=true;
        if (uniformDesign.isAskBeforeDelete()){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.initOwner(owner);
            alert.setTitle(uniformDesign.getText(deleteConfirmationTitle));
            alert.setHeaderText(uniformDesign.getText(deleteConfirmationHeader));
            alert.setContentText(uniformDesign.getText(deleteConfirmationContent));

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() == ButtonType.OK){
                reallyDelete=true;
            } else {
                reallyDelete=false;
            }
        }
        if (reallyDelete){
            final List<T> selectedItems = new ArrayList<>(tableView.getSelectionModel().getSelectedItems());
            selectedItems.forEach(t -> deleter.accept(t,list));
        }
    }

}
