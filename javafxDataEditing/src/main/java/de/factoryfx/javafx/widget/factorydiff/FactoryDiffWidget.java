package de.factoryfx.javafx.widget.factorydiff;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.factoryfx.data.merge.MergeDiffInfo;
import de.factoryfx.data.merge.MergeResultEntryInfo;
import de.factoryfx.data.util.LanguageText;
import de.factoryfx.javafx.util.UniformDesign;
import de.factoryfx.javafx.widget.Widget;
import de.factoryfx.javafx.widget.table.TableControlWidget;
import difflib.Delta;
import difflib.DiffUtils;
import difflib.Patch;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.controlsfx.glyphfont.FontAwesome;
import org.fxmisc.richtext.StyleClassedTextArea;

public class FactoryDiffWidget implements Widget {
    private UniformDesign uniformDesign;
    private LanguageText columnField=new LanguageText().en("field").de("Feld");
    private LanguageText columnPrevious=new LanguageText().en("previous").de("Alt");
    private LanguageText columnNew=new LanguageText().en("new").de("Neu");
    private LanguageText titleDiff=new LanguageText().en("difference").de("Unterschied");
    private LanguageText titlePrevious=new LanguageText().en("previous value ").de("Alter Wert");
    private LanguageText titleNew=new LanguageText().en("new value").de("Neuer Wert");
    private LanguageText noChangesFound=new LanguageText().en("No changes found").de("keine Änderungen gefunden");



    public FactoryDiffWidget(UniformDesign uniformDesign){
        this.uniformDesign=uniformDesign;
    }

    private final SimpleObjectProperty<MergeDiffInfo> mergeDiff = new SimpleObjectProperty<>();


    @Override
    public Node createContent() {
        StyleClassedTextArea previousValueDisplay = new StyleClassedTextArea ();
        previousValueDisplay.setEditable(false);
        previousValueDisplay.setOpacity(0.6);
        StyleClassedTextArea newValueDisplay = new StyleClassedTextArea ();
        newValueDisplay.setEditable(false);
        newValueDisplay.setOpacity(0.6);
        StyleClassedTextArea diffDisplay = new StyleClassedTextArea ();
        diffDisplay.getStyleClass().add("diffTextField");
        diffDisplay.setEditable(false);

        TableView<MergeResultEntryInfo> diffTableView = createDiffTableViewTable();
        final ObservableList<MergeResultEntryInfo> diffList= FXCollections.observableArrayList();
        diffTableView.setItems(diffList);


        BorderPane borderPane = new BorderPane();

        SplitPane verticalSplitPane = new SplitPane();
        verticalSplitPane.setOrientation(Orientation.VERTICAL);
        verticalSplitPane.getItems().add(diffTableView);


        VBox diffBox = new VBox(3);
        VBox.setVgrow(diffDisplay, Priority.ALWAYS);
        diffBox.getChildren().add(diffDisplay);
        HBox diffJumpButtons = new HBox(3);
        diffJumpButtons.setAlignment(Pos.CENTER);
        diffJumpButtons.setPadding(new Insets(3));
        Button up = new Button();
        uniformDesign.addIcon(up,FontAwesome.Glyph.CHEVRON_CIRCLE_UP);
        diffJumpButtons.getChildren().add(up);
        Button down = new Button();
        uniformDesign.addIcon(down,FontAwesome.Glyph.CHEVRON_CIRCLE_DOWN);
        diffJumpButtons.getChildren().add(down);
        diffBox.getChildren().add(diffJumpButtons);

        SplitPane diffValuesPane = new SplitPane();
        diffValuesPane.getItems().add(addTitle(previousValueDisplay,uniformDesign.getText(titlePrevious)));
        diffValuesPane.getItems().add(addTitle(diffBox,uniformDesign.getText(titleDiff)));
        diffValuesPane.getItems().add(addTitle(newValueDisplay,uniformDesign.getText(titleNew)));
        diffValuesPane.setDividerPositions(0.333,0.6666);
        verticalSplitPane.getItems().add(diffValuesPane);

        diffTableView.getSelectionModel().selectedItemProperty().addListener(observable -> {
            MergeResultEntryInfo diffItem=diffTableView.getSelectionModel().getSelectedItem();
            if (diffItem!=null){
                Patch<String> patch = DiffUtils.diff(
                        convertToList(diffItem.previousValueDisplayText),
                        convertToList(diffItem.newValueValueDisplayText)
                );
                String originalText=diffItem.previousValueDisplayText;

                List<StyleClassArea> styleChanges= new ArrayList<>();

                int previousOriginalPosition=0;
                StringBuilder diffString = new StringBuilder();
                String  lastOriginalStringDelta="";

                final List<Integer> diffPositions=new ArrayList<>();
                for (Delta<String> delta: patch.getDeltas()) {
                    String originalStringDelta = delta.getOriginal().getLines().stream().collect(Collectors.joining());
                    String revisitedStringDelta = delta.getRevised().getLines().stream().collect(Collectors.joining());
                    final String unchanged=originalText.substring(previousOriginalPosition,delta.getOriginal().getPosition()).replace(lastOriginalStringDelta,"");

                    diffString.append(unchanged);
                    diffPositions.add(diffString.toString().length());
                    styleChanges.add(new StyleClassArea(diffString.length()-unchanged.length(), diffString.length(), "diffUnchanged"));
                    diffString.append(originalStringDelta);
                    styleChanges.add(new StyleClassArea(diffString.length()-originalStringDelta.length(), diffString.length(), "diffOld"));
                    diffString.append(revisitedStringDelta);
                    styleChanges.add(new StyleClassArea(diffString.length()-revisitedStringDelta.length(), diffString.length(), "diffNew"));

                    previousOriginalPosition=delta.getOriginal().getPosition();
                    lastOriginalStringDelta=originalStringDelta;
                }
                final String unchanged = originalText.substring(previousOriginalPosition, originalText.length()).replace(lastOriginalStringDelta,"");
                diffString.append(unchanged);
                styleChanges.add(new StyleClassArea(diffString.length()-unchanged.length(), diffString.length(), "diffUnchanged"));

                diffDisplay.replaceText(diffString.toString());
                for (StyleClassArea styleClassArea: styleChanges){
                    diffDisplay.setStyleClass(styleClassArea.start,styleClassArea.end,styleClassArea.cssclass);
                }

                if (!patch.getDeltas().isEmpty()){
                    int firstChange=patch.getDeltas().get(0).getOriginal().getPosition();
                    diffDisplay.positionCaret(firstChange);
                    diffDisplay.selectRange(firstChange, firstChange);
                }

                diffPosition=0;
                if (!diffPositions.isEmpty()){
                    diffPosition=diffPositions.get(0);
                }
                up.setOnAction(event -> {
                    for (Integer pos: diffPositions){
                        if (pos<diffPosition){
                            diffDisplay.selectRange(pos,pos);
                            diffPosition=pos;
                            break;
                        }
                    }
                });
                down.setOnAction(event -> {
                    for (Integer pos: diffPositions){
                        if (pos>diffPosition){
                            diffDisplay.selectRange(pos,pos);
                            diffPosition=pos;
                            break;
                        }
                    }
                });

                previousValueDisplay.replaceText(diffItem.previousValueDisplayText);
                newValueDisplay.replaceText(diffItem.newValueValueDisplayText);
            }
        });


        InvalidationListener invalidationListener = observable -> {
            MergeDiffInfo mergeDiff = this.mergeDiff.get();
            if (mergeDiff != null) {
                diffList.clear();
                diffList.addAll(mergeDiff.mergeInfos);
                diffList.addAll(mergeDiff.conflictInfos);
                diffTableView.getSelectionModel().selectFirst();
            }
        };
        mergeDiff.addListener(invalidationListener);
        invalidationListener.invalidated(mergeDiff);


        borderPane.setCenter(verticalSplitPane);
        return borderPane;
    }

    int diffPosition;

    private TableView<MergeResultEntryInfo> createDiffTableViewTable(){
        TableView<MergeResultEntryInfo> tableView = new TableView<>();
        tableView.setPlaceholder(new Label(uniformDesign.getText(noChangesFound)));
        {
            TableColumn<MergeResultEntryInfo, String> fieldColumn = new TableColumn<>(uniformDesign.getText(columnField));
            fieldColumn.setCellValueFactory(param -> new SimpleStringProperty(uniformDesign.getText(param.getValue().fieldDisplayText)));
            tableView.getColumns().add(fieldColumn);

            TableColumn<MergeResultEntryInfo, String> previousColumn = new TableColumn<>(uniformDesign.getText(columnPrevious));
            previousColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().previousValueDisplayText));
            tableView.getColumns().add(previousColumn);

            TableColumn<MergeResultEntryInfo, String> newColumn = new TableColumn<>(uniformDesign.getText(columnNew));
            newColumn.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().newValueValueDisplayText));
            tableView.getColumns().add(newColumn);

        }




        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setRowFactory(new Callback<TableView<MergeResultEntryInfo>, TableRow<MergeResultEntryInfo>>() {
            @Override
            public TableRow<MergeResultEntryInfo> call(TableView<MergeResultEntryInfo> param) {
                return new TableRow<MergeResultEntryInfo>(){
                    @Override
                    protected void updateItem(MergeResultEntryInfo mergeResultEntry, boolean empty){
                        super.updateItem(mergeResultEntry, empty);
                        if (mergeResultEntry!=null && mergeResultEntry.conflict) {
                            getStyleClass().add("conflictRow");
                        } else {
                            getStyleClass().remove("conflictRow");
                        }
                    }
                };
            }
        });

        tableView.fixedCellSizeProperty().set(24);

        new TableControlWidget<>(tableView,uniformDesign).hide();
        return tableView;
    }

    private static class StyleClassArea{
        public String cssclass;
        public int start;
        public int end;

        public StyleClassArea(int start, int end, String cssclass) {
            this.cssclass = cssclass;
            this.start = start;
            this.end = end;
        }
    }

    private List<String> convertToList(String value){
        //Arrays.asList(value.split(""))  slower
        ArrayList<String> result = new ArrayList<>(value.length());
        for (int i = 0;i < value.length(); i++){
            result.add(String.valueOf(value.charAt(i)));
        }
        return result;
    }

    private Node addTitle(Node node, String title){
        VBox vBox = new VBox();
        vBox.setSpacing(3);
        Label label = new Label(title);
        label.getStyleClass().add("titleLabel");
        vBox.getChildren().add(label);
        VBox.setVgrow(node, Priority.ALWAYS);
        vBox.getChildren().add(node);
        return vBox;
    }

    public void updateMergeDiff(MergeDiffInfo mergeDiff) {
        this.mergeDiff.set(mergeDiff);
    }

}
