package com.divio.flavours.addon.model;

public class Meta {
    private final String name;
    private final String version;

    Meta() {
        name = null;
        version = null;
    }
    
    public Meta(final String name, final String version) {
        this.name = name;
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
