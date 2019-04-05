package io.github.factoryfx.javafx.javascript.editor.data;


import io.github.factoryfx.factory.FactoryBase;
import io.github.factoryfx.factory.attribute.dependency.FactoryReferenceAttribute;
import io.github.factoryfx.javascript.data.attributes.types.JavascriptAttribute;

import java.io.PrintStream;
import java.util.Arrays;

public class ExampleJavascript extends FactoryBase<Void,ExampleJavascript> {

    public final FactoryReferenceAttribute<ExampleJavascript,Void,ExampleData1> data = new FactoryReferenceAttribute<ExampleJavascript,Void,ExampleData1>().en("data");
    public final JavascriptAttribute<PrintStream> specialAttribute=new JavascriptAttribute<>(()-> Arrays.asList(data.get()),PrintStream.class).de("code");

    public ExampleJavascript() {
    }

}