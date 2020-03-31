package com.divio.flavours.fam.gradle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Set;

public class AddonConfig implements ValidatableConfig<AddonConfig> {
    @JsonProperty("spec")
    @NotBlank
    private String specValue;

    @JsonProperty("install")
    @NotNull
    @Valid
    private Install installValue;

    @JsonProperty("meta")
    @NotNull
    @Valid
    private Meta metaValue;

    @JsonProperty("config")
    private Map<String, Config> configValue;

    private AddonConfig() {
    }

    public AddonConfig(final String specValue, final Install installValue, final Meta metaValue,
                       final Map<String, Config> configValue) {
        this.specValue = specValue;
        this.installValue = installValue;
        this.metaValue = metaValue;
        this.configValue = configValue;
    }

    public Map<String, Config> getConfig() {
        return configValue;
    }

    public Install getInstall() {
        return installValue;
    }

    public Meta getMeta() {
        return metaValue;
    }

    public String getSpec() {
        return specValue;
    }

    @JsonIgnore
    public AddonConfig withConfig(final Map<String, Config> configValue) {
        return new AddonConfig(specValue, installValue, metaValue, configValue);
    }

    @Override
    public Set<ConstraintViolation<AddonConfig>> validate() {
        return Set.of();
    }
}
