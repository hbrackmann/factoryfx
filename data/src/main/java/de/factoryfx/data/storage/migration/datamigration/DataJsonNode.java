package de.factoryfx.data.storage.migration.datamigration;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import de.factoryfx.data.Data;

public class DataJsonNode {
    private final ObjectNode jsonNode;


    public DataJsonNode(ObjectNode jsonNode) {
        this.jsonNode = jsonNode;
    }

    public void removeAttribute(String name){
        jsonNode.remove(name);
    }

    public String getDataClassName(){
        return jsonNode.get("@class").textValue();
    }

    public boolean match(String dataClassNameFullQualified){
        return getDataClassName().equals(dataClassNameFullQualified);
    }

    public void renameAttribute(String previousAttributeName, String newAttributeName) {
        jsonNode.set(newAttributeName, jsonNode.get(previousAttributeName));
        jsonNode.remove(previousAttributeName);
    }

    public void renameClass(Class<? extends Data> newDataClass) {
        jsonNode.set("@class",new TextNode(newDataClass.getName()));
    }
}