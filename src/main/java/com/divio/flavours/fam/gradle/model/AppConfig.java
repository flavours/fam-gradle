package com.divio.flavours.fam.gradle.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.ConstraintViolation;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @JsonIgnore
    public AppConfig withAddons(final Map<String, AddonMeta> addonsValue) {
        return new AppConfig(specValue, metaValue, addonsValue);
    }

    @JsonIgnore
    public AppConfig addAddon(final String packageValue, final AddonMeta addonMetaValue) {
        var newAddonsValue = new HashMap<>(addonsValue);
        newAddonsValue.put(packageValue, addonMetaValue);
        return withAddons(newAddonsValue);
    }

    @JsonIgnore
    public AppConfig removeAddon(final String addonHash) {
        var newAddonsValue = addonsValue.entrySet().stream()
                .filter(es -> !es.getValue().getHash().equals(addonHash))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        return withAddons(newAddonsValue);
    }

    @JsonIgnore
    public boolean hasAddon(final String addonHash) {
        return addonsValue.entrySet().stream()
                .anyMatch(es -> es.getValue().getHash().equals(addonHash));
    }

    @Override
    public Set<ConstraintViolation<AppConfig>> validate() {
        return Set.of();
    }
}
