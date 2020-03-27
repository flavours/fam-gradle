package com.divio.flavours.fam.gradle.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;

public class AddonMeta {
    @JsonProperty("manager")
    @NotBlank
    private String managerValue;

    @JsonProperty("hash")
    @NotBlank
    private String hashValue;

    public AddonMeta() { }

    public AddonMeta(final String managerValue, final String hashValue) {
        this.managerValue = managerValue;
        this.hashValue = hashValue;
    }

    public String getManager() {
        return managerValue;
    }

    public String getHash() {
        return hashValue;
    }
}
