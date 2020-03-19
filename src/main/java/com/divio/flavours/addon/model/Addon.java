package com.divio.flavours.addon.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Addon {
    @JsonProperty("spec")
    private final String specValue;

    @JsonProperty("install")
    private final Install installValue;

    @JsonProperty("meta")
    private final Meta metaValue;

    @JsonProperty("config")
    private final Map<String, Config> configValue;

    Addon() {
        specValue = null;
        installValue = null;
        metaValue = null;
        configValue = null;
    }

    public Addon(final String specValue, final Install installValue, final Meta metaValue,
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

    @Override
    public boolean equals(final Object obj) {
        throw new RuntimeException("Not implemented");
    }
}
