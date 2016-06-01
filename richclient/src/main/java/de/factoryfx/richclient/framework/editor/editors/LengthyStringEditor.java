package de.factoryfx.richclient.framework.editor.editors;

import de.factoryfx.factory.attribute.StringAttribute;
import de.factoryfx.richclient.framework.editor.AttributeEditor;
import javafx.scene.control.TextArea;

public class LengthyStringEditor extends AttributeEditor<String, StringAttribute> {

    public LengthyStringEditor() {
        super(String.class);
    }

    @Override
    public TextArea createContent() {
        TextArea textArea = new TextArea();
        textArea.textProperty().bindBidirectional(boundTo);
        textArea.disableProperty().bind(disabledProperty());
        return textArea;
    }
}