package com.divio.flavours.addon.model;

public class ConfigBase {
    private final String spec;

    private final Install install;

    private final Meta meta;

    ConfigBase() {
        spec = null;
        install = null;
        meta = null;
    }

    public ConfigBase(final String spec, final Install install, final Meta meta) {
        this.spec = spec;
        this.install = install;
        this.meta = meta;
    }

    public Install getInstall() {
        return install;
    }

    public Meta getMeta() {
        return meta;
    }

    public String getSpec() {
        return spec;
    }
}
