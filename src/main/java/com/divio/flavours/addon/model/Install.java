package com.divio.flavours.addon.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Install {

    @JsonProperty("package")
    private final String packageValue;

    Install() {
        packageValue = null;
    }

    public Install(final String packageValue) {
        this.packageValue = packageValue;
    }

    public String getPackage() {
        return this.packageValue;
    }

}
