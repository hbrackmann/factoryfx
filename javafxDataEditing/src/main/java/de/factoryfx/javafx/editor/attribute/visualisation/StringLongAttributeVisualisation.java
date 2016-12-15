package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextArea;

public class StringLongAttributeVisualisation extends ValueAttributeEditorVisualisation<String> {


    @Override
    public Node createContent(SimpleObjectProperty<String> attributeValue) {
        TextArea textField = new TextArea();
        textField.textProperty().bindBidirectional(attributeValue);
        return textField;
    }
}
