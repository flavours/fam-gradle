package com.divio.flavours.addon.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Config {
    @JsonProperty("label")
    private final String labelValue;

    @JsonProperty("required")
    private final boolean requiredValue;

    @JsonProperty("type")
    private final String typeValue; // TODO: use enum

    @JsonProperty("default")
    private final String defaultValue;

    @JsonProperty("helptext")
    private final String helpTextValue;

    @JsonProperty("min")
    private final Integer minValue;

    Config() {
        this.labelValue = null;
        this.requiredValue = false;
        this.typeValue = null;
        this.defaultValue = null;
        this.helpTextValue = null;
        this.minValue = null;
    }

    public Config(String labelValue, boolean requiredValue, String typeValue, String defaultValue, String helpTextValue,
            Integer minValue) {
        this.labelValue = labelValue;
        this.requiredValue = requiredValue;
        this.typeValue = typeValue;
        this.defaultValue = defaultValue;
        this.helpTextValue = helpTextValue;
        this.minValue = minValue;
    }

    public String getDefault() {
        return defaultValue;
    }

    public String getLabel() {
        return labelValue;
    }

    public boolean isRequired() {
        return requiredValue;
    }

    public String getType() {
        return typeValue;
    }

    public String getHelpText() {
        return helpTextValue;
    }

    public Integer getMinValue() {
        return minValue;
    }
}
