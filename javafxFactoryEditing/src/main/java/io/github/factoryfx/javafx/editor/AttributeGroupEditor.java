package io.github.factoryfx.javafx.editor;

import com.google.common.base.Strings;
import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.Attribute;
import io.github.factoryfx.factory.attribute.AttributeAndMetadata;
import io.github.factoryfx.factory.validation.ValidationError;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisationMappingBuilder;
import io.github.factoryfx.javafx.editor.attribute.AttributeVisualisation;
import io.github.factoryfx.javafx.util.UniformDesign;
import io.github.factoryfx.javafx.widget.Widget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** to edit attributes independent af the parent factory*/
public class AttributeGroupEditor implements Widget {
    private final List<AttributeAndMetadata> attributeGroup;
    private final FactoryBase<?,?> oldValue;
    private final AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder;
    private final Consumer<FactoryBase<?,?>> navigateToData;
    private final UniformDesign uniformDesign;
    /** parent data validation that includes multi attribute validation and single attribute validation */
    private final Supplier<List<ValidationError>> allValidations;

    public AttributeGroupEditor(List<AttributeAndMetadata> attributeGroup, FactoryBase<?,?> oldValue, AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder, Consumer<FactoryBase<?,?>> navigateToData, UniformDesign uniformDesign, Supplier<List<ValidationError>> additionalValidation) {
        this.attributeGroup = attributeGroup;
        this.oldValue = oldValue;
        this.attributeVisualisationMappingBuilder = attributeVisualisationMappingBuilder;
        this.navigateToData = navigateToData;
        this.uniformDesign = uniformDesign;
        this.allValidations = additionalValidation;
    }


    public AttributeGroupEditor(List<AttributeAndMetadata> attributeGroup, FactoryBase<?,?> oldValue, AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder, DataEditor dataEditor, UniformDesign uniformDesign, Supplier<List<ValidationError>> additionalValidation) {
        this(attributeGroup,oldValue, attributeVisualisationMappingBuilder, dataEditor::navigate, uniformDesign, additionalValidation);
    }

    /** constructor for use as single independent component
     * @param attributeGroup attributeGroup
     * @param attributeVisualisationMappingBuilder attributeEditorBuilder
     * @param uniformDesign uniformDesign
     * */
    public AttributeGroupEditor(List<AttributeAndMetadata> attributeGroup, AttributeVisualisationMappingBuilder attributeVisualisationMappingBuilder, UniformDesign uniformDesign) {
        this(attributeGroup, null, attributeVisualisationMappingBuilder, (data) -> {
        }, uniformDesign, () -> {
            ArrayList<ValidationError> result = new ArrayList<>();
            for (AttributeAndMetadata immutableValueAttribute : attributeGroup) {
                result.addAll(immutableValueAttribute.attribute.internal_validate(null,""));
            }
            return result;
        });
    }

    @Override
    public Node createContent() {
        return createAttributeGroupVisual();
    }

    private List<AttributeVisualisation> createdVisualisations=new ArrayList<>();//prevent gc collection, javafx bindings are weak

    private Node createAttributeGroupVisual() {
//        if (attributeGroup.size()==1){
//            final Attribute<?,?> attribute = attributeGroup.get(0);
//            AttributeEditor<?,?> attributeEditor = attributeEditorBuilder.getAttributeVisualisation(attribute, navigateToData, oldValue);
//            attributeEditor.expand();
//
//            addAttributeValidation(attribute, attributeEditor);
//
//            final Node content = attributeEditor.createContent();
//            final VBox vBox = new VBox(3);
//            vBox.setPadding(new Insets(3));
//            VBox.setVgrow(content, Priority.ALWAYS);
//            vBox.getChildren().addAll(addCopyMenu(new Label(uniformDesign.getLabelText(attribute))),content);
//
//            if (allValidations!=null) {
//                validateAll();
//            }
//            return vBox;

//        } else {
            GridPane grid = new GridPane();
            grid.setPadding(new Insets(0, 0, 0, 0));

            ColumnConstraints column1 = new ColumnConstraints();
            column1.setHgrow(Priority.SOMETIMES);
            column1.setMinWidth(100);
            column1.setPrefWidth(200);
            ColumnConstraints column2 = new ColumnConstraints();
            column2.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(column1, column2);

            createdVisualisations.clear();
            int row = 0;
            for (AttributeAndMetadata attributeAndMetadata: attributeGroup){
                Label label = addLabelContent(grid, row,attributeAndMetadata.attribute);
                addCopyMenu(label);
                AttributeVisualisation attributeVisualisation  = attributeVisualisationMappingBuilder.getAttributeVisualisation(attributeAndMetadata.attribute,attributeAndMetadata.attributeMetadata,navigateToData,oldValue);
                createdVisualisations.add(attributeVisualisation);

                int rowFinal=row;
//                createdEditors.put(attribute,attributeEditor);
                addEditorContent(grid, rowFinal, attributeVisualisation.createVisualisation(),label);

                RowConstraints rowConstraints = new RowConstraints();
                rowConstraints.setVgrow(Priority.ALWAYS);
                grid.getRowConstraints().add(rowConstraints);
                row++;

                if (attributeGroup.size()==1) {
                    attributeVisualisation.expand();
                }
            }

//            for (RowConstraints rowConstraint: grid.getRowConstraints()){
//                rowConstraint.setMinHeight(36);
//            }



            return wrapGrid(grid);
//        }
    }

    private Label addCopyMenu(Label label) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem copyItem = new MenuItem("copy");
        copyItem.setOnAction(event -> {
            final Clipboard clipboard = Clipboard.getSystemClipboard();
            final ClipboardContent content = new ClipboardContent();
            content.putString(label.getText().replaceFirst("_",""));
            clipboard.setContent(content);
        });
        contextMenu.getItems().add(copyItem);
        label.setContextMenu(contextMenu);

        return label;
    }


    private void addEditorContent(GridPane gridPane, int row, Node editorWidgetContent, Label label) {
//        GridPane.setMargin(editorWidgetContent, new Insets(4, 0, 4, 0));
        label.setLabelFor(editorWidgetContent);

        StackPane pane = new StackPane();
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.getChildren().add(editorWidgetContent);
        pane.setPadding(new Insets(5,3,5,0));
        gridPane.add(pane, 1, row);

        if (row%2==0) {
            pane.setStyle("-fx-background-color: "+ highlightBackground + ";");
        }
    }

    private Label addLabelContent(GridPane gridPane, int row, Attribute<?,?> attribute) {
        String mnemonicLabelText=uniformDesign.getLabelText(attribute);
        if (mnemonicLabelText!=null){
            mnemonicLabelText="_"+mnemonicLabelText;
        }
        Label label = new Label(mnemonicLabelText);
        label.setMnemonicParsing(true);
        label.setWrapText(true);
//        label.setTextOverrun(OverrunStyle.CLIP);
        GridPane.setMargin(label, new Insets(0, 9, 0, 0));
        StackPane pane = new StackPane();
        pane.setPadding(new Insets(5,3,5,3));
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.getChildren().add(label);
        gridPane.add(pane, 0, row);

        if (row%2==0) {
            pane.setStyle("-fx-background-color: " + highlightBackground + ";");
        }
        String tooltip=uniformDesign.getTooltipText(attribute);
        if (!Strings.isNullOrEmpty(tooltip)) {
            label.setTooltip(new Tooltip(tooltip));
        }

        return label;
    }
    final static String highlightBackground = "#FCFCFC";

    private Node wrapGrid(GridPane gridPane){
        VBox vBox = new VBox();
        vBox.getChildren().add(new Separator());
        vBox.getChildren().add(gridPane);
        vBox.getChildren().add(new Separator());

        ScrollPane scrollPane = new ScrollPane(vBox);
        scrollPane.setFitToWidth(true);
//        root.setFitToHeight(scrollPaneFitToHeight);
        scrollPane.setStyle("-fx-background-color:transparent;");//hide border
//        root.disableProperty().edit(disabledProperty());
        return scrollPane;
    }

    public void destroy() {
        for (AttributeVisualisation createdVisualisation : createdVisualisations) {
            createdVisualisation.destroy();
        }
    }
}
