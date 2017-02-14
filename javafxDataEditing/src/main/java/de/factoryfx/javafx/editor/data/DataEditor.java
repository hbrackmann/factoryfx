package de.factoryfx.javafx.editor.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import de.factoryfx.data.Data;
import de.factoryfx.data.attribute.Attribute;
import de.factoryfx.data.attribute.AttributeChangeListener;
import de.factoryfx.data.attribute.WeakAttributeChangeListener;
import de.factoryfx.data.validation.ValidationError;
import de.factoryfx.javafx.editor.attribute.AttributeEditor;
import de.factoryfx.javafx.editor.attribute.AttributeEditorFactory;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import impl.org.controlsfx.skin.BreadCrumbBarSkin;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.controlsfx.control.BreadCrumbBar;
import org.controlsfx.glyphfont.FontAwesome;

public class DataEditor implements Widget {
    static final int HISTORY_LIMIT = 20;

    private final AttributeEditorFactory attributeEditorFactory;
    private final UniformDesign uniformDesign;
    SimpleObjectProperty<Data> bound = new SimpleObjectProperty<>();
    private ChangeListener<Data> dataChangeListener;
    private AttributeChangeListener validationListener;

    public DataEditor(AttributeEditorFactory attributeEditorFactory, UniformDesign uniformDesign) {
        this.attributeEditorFactory = attributeEditorFactory;
        this.uniformDesign = uniformDesign;
    }

    public ReadOnlyObjectProperty<Data> editData(){
        return bound;
    }

    public void edit(Data newValue) {
        Data current = bound.get();
        bound.set(newValue);
        if (!displayedEntities.contains(newValue)){
            removeUpToCurrent(current);
            displayedEntities.add(newValue);
            if (displayedEntities.size()>HISTORY_LIMIT){
                displayedEntities.remove(0);
            }
        } else {
            int indexOfCurrent = displayedEntities.indexOf(current);
            int indexOfNewValue = displayedEntities.indexOf(newValue);
            if (indexOfNewValue > indexOfCurrent) {
                removeUpToCurrent(current);
                displayedEntities.add(newValue);
                if (displayedEntities.size()>HISTORY_LIMIT){
                    displayedEntities.remove(0);
                }
            }
        }

    }

    public void editExisting(Data newValue) {
        bound.set(newValue);
    }

    public void resetHistory(){
        displayedEntities.setAll(bound.get());
    }
    public void setHistory(List<Data> data){
        displayedEntities.addAll(data);
    }

    public void reset(){
        displayedEntities.clear();
        bound.set(null);
    }

    private void removeUpToCurrent(Data current) {
        if (current == null)
            return;
        int idx = displayedEntities.indexOf(current);
        if (idx >= 0) {
            displayedEntities.remove(idx+1,displayedEntities.size());
        }
    }


    HashMap<Attribute<?>,AttributeEditor<?>> createdEditors=new HashMap<>();

    private Node wrapGrid(GridPane gridPane){
        ScrollPane scrollPane = new ScrollPane(gridPane);
        scrollPane.setFitToWidth(true);
//        root.setFitToHeight(scrollPaneFitToHeight);
        scrollPane.setStyle("-fx-background-color:transparent;");//hide border
//        root.disableProperty().edit(disabledProperty());
        return scrollPane;
    }


//    List<ValidationError> attributeValidationError = new ArrayList<>();
//    for (ValidationError validationError: validation.get()){
//        final Attribute<?> attributeItem = validationError.attribute;
//        if (attribute==attributeItem){
//            attributeValidationError.add(validationError);
//        }
//    }
//    validationResult.set(attributeValidationError);

    BiFunction<Node,Data,Node> visCustomizer= (node, data) -> node;

    public void setVisCustomizer(BiFunction<Node,Data,Node> visCustomizer){
        this.visCustomizer=visCustomizer;
    }

    private Node customizeVis(Node defaultVis, Data data){
        return visCustomizer.apply(defaultVis,data);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Node createContent() {
        BorderPane result = new BorderPane();

        if (dataChangeListener!=null) {
            bound.removeListener(dataChangeListener);
        }

        BiConsumer<Node,Data> updateVis= (defaultVis, data) -> {
            result.setCenter(customizeVis(defaultVis,data));
        };

        dataChangeListener = (observable, oldValue, newValue) -> {

            createdEditors.entrySet().forEach(entry -> entry.getValue().unbind());
            createdEditors.clear();
            if (newValue==null) {
                result.setCenter(new Label("empty"));
            } else {

                if (newValue.internal().attributeListGrouped().size()==1){
                    final Node attributeGroupVisual = createAttributeGroupVisual(newValue.internal().attributeListGrouped().get(0).getValue(), () -> newValue.internal().validateFlat(),oldValue);
                    updateVis.accept(attributeGroupVisual,newValue);
                } else {
                    TabPane tabPane = new TabPane();
                    for (Pair<String,List<Attribute<?>>> attributeGroup: newValue.internal().attributeListGrouped()) {
                        Tab tab=new Tab(attributeGroup.getKey());
                        tab.setClosable(false);
                        tab.setContent(createAttributeGroupVisual(attributeGroup.getValue(),() -> newValue.internal().validateFlat(),oldValue));
                        tabPane.getTabs().add(tab);
                    }
                    updateVis.accept(tabPane,newValue);
                }

                if (validationListener!=null && oldValue!=null){
                    oldValue.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                        attribute.internal_removeListener(validationListener);
                    });
                }
                validationListener = (attribute1, value) -> {
                    final List<ValidationError> validationErrors = newValue.internal().validateFlatForAttribute(attribute1);

                    //if child data has errors we want to show that as well
                    List<Data> childrenData = new ArrayList<>();
                    if (value instanceof Data){
                        childrenData.add((Data)value);
                    }
                    if (value instanceof List){
                        ((List)value).forEach(data -> {
                            if (data instanceof Data){
                                childrenData.add((Data)data);
                            }
                        });
                    }
                    childrenData.forEach(data -> validationErrors.addAll(data.internal().validateFlat()));

                    final AttributeEditor<?> attributeEditor = createdEditors.get(attribute1);
                    if (attributeEditor!=null){
                        attributeEditor.reportValidation(validationErrors);
                    }

                };
                newValue.internal().visitAttributesFlat((attributeVariableName, attribute) -> {
                    attribute.internal_addListener(new WeakAttributeChangeListener<>(validationListener));
                    validationListener.changed(attribute,attribute.get());
                });


            }


        };
        bound.addListener(dataChangeListener);
        dataChangeListener.changed(bound,bound.get(),bound.get());



        result.setTop(createNavigation());
        return result;
    }


    private Node createAttributeGroupVisual(List<Attribute<?>> attributeGroup, Supplier<List<ValidationError>> validation, Data oldValue) {
        if (attributeGroup.size()==1){
            final Attribute<?> attribute = attributeGroup.get(0);
            Optional<AttributeEditor<?>> attributeEditor = attributeEditorFactory.getAttributeEditor(attribute, this, validation, oldValue);
            if (attributeEditor.isPresent()){
                attributeEditor.get().expand();
                createdEditors.put(attribute,attributeEditor.get());
                final Node content = attributeEditor.get().createContent();
                final VBox vBox = new VBox(3);
                vBox.setPadding(new Insets(3));
                VBox.setVgrow(content,Priority.ALWAYS);
                vBox.getChildren().addAll(new Label(uniformDesign.getLabelText(attribute)),content);
                return vBox;
            } else {
                return new Label("unsupported attribute:"+attribute.internal_getAttributeType().dataType);
            }
        } else {
            GridPane grid = new GridPane();
//        grid.setHgap(3);
//             grid.setVgap(3);
            grid.setPadding(new Insets(3, 3, 3, 3));

            ColumnConstraints column1 = new ColumnConstraints();
            column1.setHgrow(Priority.SOMETIMES);
            column1.setMinWidth(100);
            column1.setPrefWidth(200);
            ColumnConstraints column2 = new ColumnConstraints();
            column2.setHgrow(Priority.ALWAYS);
            grid.getColumnConstraints().addAll(column1, column2);


            int row = 0;
            for (Attribute<?> attribute: attributeGroup){
                Label label = addLabelContent(grid, row,uniformDesign.getLabelText(attribute));

                Optional<AttributeEditor<?>> attributeEditor = attributeEditorFactory.getAttributeEditor(attribute,this,validation,oldValue);
                int rowFinal=row;
                if (attributeEditor.isPresent()){
                    createdEditors.put(attribute,attributeEditor.get());
                    addEditorContent(grid, rowFinal, attributeEditor.get().createContent(),label);
                } else {
                    addEditorContent(grid, rowFinal, new Label("unsupported attribute:"+attribute.internal_getAttributeType().dataType+", "+attribute.internal_getAttributeType().listItemType),label);
                }

                RowConstraints rowConstraints = new RowConstraints();
                rowConstraints.setVgrow(Priority.ALWAYS);
                grid.getRowConstraints().add(rowConstraints);

                row++;
            }

            for (RowConstraints rowConstraint: grid.getRowConstraints()){
                rowConstraint.setMinHeight(36);
            }

            return wrapGrid(grid);
        }
    }

    private Label addLabelContent(GridPane gridPane, int row,String text) {
        String mnemonicLabelText=text;
        if (mnemonicLabelText!=null){
            mnemonicLabelText="_"+mnemonicLabelText;
        }
        Label label = new Label(mnemonicLabelText);
        label.setMnemonicParsing(true);
//        if (icon!=null){
//            label.setGraphic(icon.get());
//        }
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
    }


    ObservableList<Data> displayedEntities= FXCollections.observableArrayList();
    private Node createNavigation(){
        BreadCrumbBarWidthFixed<Data> breadCrumbBar = new BreadCrumbBarWidthFixed<>();


        ScrollPane scrollPaneBreadCrumbBar = new ScrollPane();
        scrollPaneBreadCrumbBar.setContent(breadCrumbBar);
        scrollPaneBreadCrumbBar.setHvalue(1.0);
        scrollPaneBreadCrumbBar.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneBreadCrumbBar.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPaneBreadCrumbBar.getStyleClass().add("transparent-scroll-pane");

        //force hvalue to 1 cause its buggy
        scrollPaneBreadCrumbBar.hvalueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue!=null && newValue.doubleValue()<1.0){
                scrollPaneBreadCrumbBar.setHvalue(1);
            }
        });

        breadCrumbBar.setCrumbFactory(param -> {
            BreadCrumbBarSkin.BreadCrumbButton breadCrumbButton = new BreadCrumbBarSkin.BreadCrumbButton("");
            if (param.getValue()!=null){
                breadCrumbButton.textProperty().bind(param.getValue().internal().getDisplayTextObservable());
            }
            if (bound.get()==param.getValue()){
                breadCrumbButton.setStyle("-fx-font-weight: bold;");
            }
            return breadCrumbButton;
        });
        breadCrumbBar.setOnCrumbAction(event -> {
            edit(event.getSelectedCrumb().getValue());
//            breadCrumbBar.setSelectedCrumb(BreadCrumbBar.buildTreeModel(displayedEntities.toArray(new Data[0])));
        });

        Runnable updateBreadCrumbBar= () -> {
            List<Data> newhistory = new ArrayList<>();
            for (Data data: displayedEntities){
                newhistory.add(data);
                if (data==bound.get()){
                    break;
                }
            }

            breadCrumbBar.setSelectedCrumb(BreadCrumbBar.buildTreeModel(newhistory.toArray(new Data[0])));
            breadCrumbBar.layout();
        };
        displayedEntities.addListener((ListChangeListener<Data>) c -> {
            updateBreadCrumbBar.run();
        });
        bound.addListener(observable -> {
            updateBreadCrumbBar.run();
        });



        HBox navigation = new HBox(3);
        navigation.getStyleClass().add("navigationhbox");
        navigation.setAlignment(Pos.TOP_LEFT);
        Button back = new Button("",uniformDesign.createIcon(FontAwesome.Glyph.CARET_LEFT).size(18));
        back.setOnAction(event -> back());
        BooleanBinding backDisabled = Bindings.createBooleanBinding(() -> !previousData().isPresent(),displayedEntities,bound);
        back.disableProperty().bind(backDisabled);
        Button next = new Button("",uniformDesign.createIcon(FontAwesome.Glyph.CARET_RIGHT).size(18));
        BooleanBinding nextDisabled = Bindings.createBooleanBinding(() -> !nextData().isPresent(),displayedEntities,bound);
        next.disableProperty().bind(nextDisabled);
        next.setOnAction(event -> next());
        navigation.getChildren().add(back);
        navigation.getChildren().add(next);


        navigation.getChildren().add(scrollPaneBreadCrumbBar);

        navigation.setPadding(new Insets(3,3,0,3));//workaround scrollpane fittoheight and  .setAlignment(Pos.TOP_LEFT); dont work in this cas

        HBox.setHgrow(scrollPaneBreadCrumbBar,Priority.ALWAYS);
        return navigation;
    }

    private Optional<Data> previousData(){
        int index = displayedEntities.indexOf(bound.get())-1;
        if (index>=0){
            return Optional.ofNullable(displayedEntities.get(index));
        }
        return Optional.empty();
    }

    private Optional<Data> nextData(){
        int index = displayedEntities.indexOf(bound.get())+1;
        if (index<displayedEntities.size()){
            return Optional.ofNullable(displayedEntities.get(index));
        }
        return Optional.empty();
    }

    void back(){
        previousData().ifPresent(this::editExisting);
    }

    void next(){
        nextData().ifPresent(this::editExisting);
    }

}
