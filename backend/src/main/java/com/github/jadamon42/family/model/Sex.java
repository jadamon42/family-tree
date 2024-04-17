package com.github.jadamon42.family.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.jadamon42.family.util.SexConverter;

@JsonDeserialize(using = SexConverter.class)
public enum Sex {
    MALE("Male"),
    FEMALE("Female"),
    UNKNOWN("Unknown");

    private final String label;

    Sex(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
