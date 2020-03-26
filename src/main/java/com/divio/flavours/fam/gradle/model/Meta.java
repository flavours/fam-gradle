package com.divio.flavours.fam.gradle.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class Meta {
    @JsonProperty("name")
    @NotBlank
    private String nameValue;

    @JsonProperty("version")
    @NotBlank
    private String versionValue;

    public Meta() { }

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
