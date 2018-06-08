package de.factoryfx.data.attribute.types;

import java.util.Collections;
import java.util.List;

import de.factoryfx.data.attribute.ValueListAttribute;

public class StringListAttribute extends ValueListAttribute<String,StringListAttribute> {

    public StringListAttribute() {
        super(String.class);
    }


    public List<String> asUnmodifiableList() {
        return Collections.unmodifiableList(get());
    }

}
