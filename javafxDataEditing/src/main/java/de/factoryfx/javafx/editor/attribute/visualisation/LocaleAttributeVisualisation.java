package de.factoryfx.javafx.editor.attribute.visualisation;

import java.text.DateFormat;
import java.util.Locale;

import de.factoryfx.javafx.editor.attribute.AttributeEditorVisualisation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;

public class LocaleAttributeVisualisation implements AttributeEditorVisualisation<Locale> {

    @Override
    public Node createContent(SimpleObjectProperty<Locale> boundTo) {
        ComboBox<Locale> comboBox=new ComboBox();
        comboBox.getItems().addAll(DateFormat.getAvailableLocales());
        comboBox.valueProperty().bindBidirectional(boundTo);
        return comboBox;
    }
}
