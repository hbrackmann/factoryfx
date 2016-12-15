package de.factoryfx.javafx.editor.attribute.visualisation;

import de.factoryfx.javafx.editor.attribute.ValueAttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class ColorAttributeVisualisation extends ValueAttributeEditorVisualisation<Color> {

    @Override
    public Node createContent(SimpleObjectProperty<Color> boundTo) {
        ColorPicker colorPicker = new ColorPicker();
        colorPicker.valueProperty().bindBidirectional(boundTo);
        return colorPicker;
    }
}
