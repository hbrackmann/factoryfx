module de.factoryfx.javafxDataEditing {
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive de.factoryfx.data;
    requires transitive javafx.web;
    requires transitive javafx.fxml;
    requires com.google.common;
    requires transitive org.controlsfx.controls;

    exports de.factoryfx.javafx.data.attribute;
    exports de.factoryfx.javafx.data.editor.attribute;
    opens de.factoryfx.javafx.data.editor.attribute;
    exports de.factoryfx.javafx.data.editor.attribute.builder;
    exports de.factoryfx.javafx.data.editor.attribute.visualisation;
    exports de.factoryfx.javafx.data.editor.attribute.converter;
    exports de.factoryfx.javafx.data.editor.data;

    exports de.factoryfx.javafx.data.util;
    exports de.factoryfx.javafx.data.widget;
    exports de.factoryfx.javafx.data.widget.datalistedit;
    exports de.factoryfx.javafx.data.widget.dataview;
    exports de.factoryfx.javafx.data.widget.factorydiff;
    exports de.factoryfx.javafx.data.widget.select;
    exports de.factoryfx.javafx.data.widget.table;
    exports de.factoryfx.javafx.data.widget.tree;
    exports de.factoryfx.javafx.data.widget.validation;

    exports de.factoryfx.javafx.css;
    opens de.factoryfx.javafx.css;

}