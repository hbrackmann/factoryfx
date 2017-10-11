package de.factoryfx.javafx.editor.data;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.data.attribute.ImmutableValueAttribute;
import de.factoryfx.data.attribute.WeakAttributeChangeListener;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.AttributeEditorBuilder;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** to edit attributes indepentend af the parent factory*/
public class AttributeGroupEditor implements Widget {
    private final List<? extends Attribute<?,?>> attributeGroup;
    private final Data oldValue;
    private final AttributeEditorBuilder attributeEditorBuilder;
    private final Consumer<Data> navigateToData;
    private final UniformDesign uniformDesign;
    /** parent data validation that includes multi attribute validation and single attribute validation */
    private final Supplier<List<ValidationError>> allValidations;

    public AttributeGroupEditor(List<? extends Attribute<?, ?>> attributeGroup, Data oldValue, AttributeEditorBuilder attributeEditorBuilder, Consumer<Data> navigateToData, UniformDesign uniformDesign, Supplier<List<ValidationError>> additionalValidation) {
        this.attributeGroup = attributeGroup;
        this.oldValue = oldValue;
        this.attributeEditorBuilder = attributeEditorBuilder;
        this.navigateToData = navigateToData;
        this.uniformDesign = uniformDesign;
        this.allValidations = additionalValidation;
    }


    public AttributeGroupEditor(List<? extends Attribute<?, ?>> attributeGroup, Data oldValue, AttributeEditorBuilder attributeEditorBuilder, DataEditor dataEditor, UniformDesign uniformDesign, Supplier<List<ValidationError>> additionalValidation) {
        this(attributeGroup,oldValue,attributeEditorBuilder,(data)->dataEditor.edit(data),uniformDesign,additionalValidation);
    }

    /** constructor for use as single independent component
     * @param attributeGroup attributeGroup
     * @param attributeEditorBuilder attributeEditorBuilder
     * @param uniformDesign uniformDesign
     * */
    public AttributeGroupEditor(List<? extends ImmutableValueAttribute<?, ?>> attributeGroup, AttributeEditorBuilder attributeEditorBuilder, UniformDesign uniformDesign) {
        this(attributeGroup, null, attributeEditorBuilder, (data) -> {
        }, uniformDesign, () -> {
            ArrayList<ValidationError> result = new ArrayList<>();
            for (ImmutableValueAttribute<?, ?> immutableValueAttribute : attributeGroup) {
                result.addAll(immutableValueAttribute.internal_validate(null));
            }
            return result;
        });
    }

    @Override
    public Node createContent() {
        return createAttributeGroupVisual();
    }

    private Node createAttributeGroupVisual() {
        if (attributeGroup.size()==1){
            final Attribute<?,?> attribute = attributeGroup.get(0);
            AttributeEditor<?,?> attributeEditor = attributeEditorBuilder.getAttributeEditor(attribute, navigateToData, oldValue);
            attributeEditor.expand();

            addAttributeValidation(attribute, attributeEditor);

            final Node content = attributeEditor.createContent();
            final VBox vBox = new VBox(3);
            vBox.setPadding(new Insets(3));
            VBox.setVgrow(content, Priority.ALWAYS);
            vBox.getChildren().addAll(new Label(uniformDesign.getLabelText(attribute)),content);

            if (allValidations!=null) {
                validateAll();
            }
            return vBox;

        } else {
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(3, 3, 3, 3));

            ColumnConstraints column1 = new ColumnConstraints();
            column1.setHgrow(Priority.SOMETIMES);
            column1.setMinWidth(100);
            column1.setPrefWidth(200);
            ColumnConstraints column2 = new ColumnConstraints();
            column2.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(column1, column2);


            int row = 0;
            for (Attribute<?,?> attribute: attributeGroup){
                Label label = addLabelContent(grid, row,uniformDesign.getLabelText(attribute));
                AttributeEditor<?,?> attributeEditor = attributeEditorBuilder.getAttributeEditor(attribute,navigateToData,oldValue);
                addAttributeValidation(attribute, attributeEditor);

                int rowFinal=row;
//                createdEditors.put(attribute,attributeEditor);
                addEditorContent(grid, rowFinal, attributeEditor.createContent(),label);

                RowConstraints rowConstraints = new RowConstraints();
                rowConstraints.setVgrow(Priority.ALWAYS);
                grid.getRowConstraints().add(rowConstraints);
                row++;
            }

            for (RowConstraints rowConstraint: grid.getRowConstraints()){
                rowConstraint.setMinHeight(36);
            }

            if (allValidations!=null) {
                validateAll();
            }
            return wrapGrid(grid);
        }
    }

    private Map<Attribute<?, ?>,AttributeEditor<?, ?>> attributeToEditor = new HashMap<>();
    private AttributeChangeListener attributeChangeListener=null;
    @SuppressWarnings("unchecked")
    private void addAttributeValidation(Attribute<?, ?> attribute, AttributeEditor<?, ?> attributeEditor) {
        attributeToEditor.put(attribute,attributeEditor);

        if (attributeChangeListener==null) {
            this.attributeChangeListener = (watchedAttribute, value) -> {
                validateAll();
            };
        }

        attribute.internal_addListener(new WeakAttributeChangeListener(attributeChangeListener));
    }

    private void validateAll() {
        List<ValidationError> validationErrors = allValidations.get();
        for (Map.Entry<Attribute<?, ?>,AttributeEditor<?, ?>> entry: attributeToEditor.entrySet()){
            List<ValidationError> attributeValidationErrors = new ArrayList<>();
            validationErrors.forEach(validationError -> {
                if (validationError.isErrorFor(entry.getKey())) {
                    attributeValidationErrors.add(validationError);
                }
            });
            entry.getValue().reportValidation(attributeValidationErrors);
        }
    }

    private void addEditorContent(GridPane gridPane, int row, Node editorWidgetContent, Label label) {
        GridPane.setMargin(editorWidgetContent, new Insets(4, 0, 4, 0));
//        label.setLabelFor(editorWidgetContent);

        StackPane pane = new StackPane();
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.getChildren().add(editorWidgetContent);
        pane.setPadding(new Insets(3,0,3,0));
        gridPane.add(pane, 1, row);

        if (row%2==0) {
            pane.setStyle("-fx-background-color: "+ highlightBackground + ";");
        }
    }    private Label addLabelContent(GridPane gridPane, int row,String text) {
        String mnemonicLabelText=text;
        if (mnemonicLabelText!=null){
            mnemonicLabelText="_"+mnemonicLabelText;
        }
        Label label = new Label(mnemonicLabelText);
        label.setMnemonicParsing(true);
        label.setWrapText(true);
//        label.setTextOverrun(OverrunStyle.CLIP);
        GridPane.setMargin(label, new Insets(0, 9, 0, 0));
        StackPane pane = new StackPane();
        pane.setPadding(new Insets(3,3,3,0));
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.getChildren().add(label);
        gridPane.add(pane, 0, row);

        if (row%2==0) {
            pane.setStyle("-fx-background-color: " + highlightBackground + ";");
        }
        return label;
    }
    final static String highlightBackground = "#FCFCFC";

    private Node wrapGrid(GridPane gridPane){
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
//        root.setFitToHeight(scrollPaneFitToHeight);
        scrollPane.setStyle("-fx-background-color:transparent;");//hide border
//        root.disableProperty().edit(disabledProperty());
        return scrollPane;
    }
}