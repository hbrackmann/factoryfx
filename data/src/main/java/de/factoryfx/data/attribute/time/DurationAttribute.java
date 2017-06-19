package de.factoryfx.data.attribute.time;

import java.time.Duration;

import com.fasterxml.jackson.annotation.JsonCreator;

import de.factoryfx.data.attribute.ImmutableValueAttribute;

/**
 * Created by mhavlik on 16.06.17.
 */
public class DurationAttribute extends ImmutableValueAttribute<Duration,DurationAttribute> {

    public DurationAttribute() {
        super(Duration.class);
    }

    @JsonCreator
    DurationAttribute(Duration initialValue) {
        super(Duration.class);
        set(initialValue);
    }

    @Override
    protected DurationAttribute createNewEmptyInstance() {
        return new DurationAttribute();
    }
}
