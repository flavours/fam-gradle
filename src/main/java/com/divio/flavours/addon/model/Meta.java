package com.divio.flavours.addon.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Meta {
    @JsonProperty("name")
    private final String nameValue;

    @JsonProperty("version")
    private final String versionValue;

    Meta() {
        nameValue = null;
        versionValue = null;
    }
    
    public Meta(final String nameValue, final String versionValue) {
        this.nameValue = nameValue;
        this.versionValue = versionValue;
    }

    public String getName() {
        return nameValue;
    }

    public String getVersion() {
        return versionValue;
    }
}
