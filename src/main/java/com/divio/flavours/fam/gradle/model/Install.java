package com.divio.flavours.fam.gradle.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class Install {
    @JsonProperty("package")
    @NotBlank
    private String packageValue;

    Install() {}

    public Install(final String packageValue) {
        this.packageValue = packageValue;
    }

    public String getPackage() {
        return this.packageValue;
    }

}
