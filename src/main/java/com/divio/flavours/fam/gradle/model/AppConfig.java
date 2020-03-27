package com.divio.flavours.fam.gradle.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

public class AppConfig implements ValidatableConfig<AppConfig> {
    @JsonProperty("spec")
    @NotBlank
    private String specValue;

    @JsonProperty("meta")
    @NotNull
    @Valid
    private Meta metaValue;

    @JsonProperty("addons")
    private Map<String, AddonMeta> addonsValue;

    private AppConfig() {
    }

    public AppConfig(final String specValue, final Meta metaValue, final Map<String, AddonMeta> addonsValue) {
        this.specValue = specValue;
        this.metaValue = metaValue;
        this.addonsValue = addonsValue;
    }

    public Map<String, AddonMeta> getAddons() {
        return addonsValue;
    }

    public Meta getMeta() {
        return metaValue;
    }

    public String getSpec() {
        return specValue;
    }

    @Override
    public Set<ConstraintViolation<AppConfig>> validate() {
        return Set.of();
    }
}
