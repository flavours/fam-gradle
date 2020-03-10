package com.divio.flavours.addon.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import org.junit.Test;

public class ConfigBaseTest {

    protected List<String> readResource(String resourceName) throws IOException {
        var in = getClass().getClassLoader().getResourceAsStream(resourceName);
        assertNotNull(in);
        try (var bin = new BufferedReader(new InputStreamReader(in))) {
            return bin.lines().collect(Collectors.toList());
        }
    }

    @Test
    public void canParseConfigBase() throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory());
        var in = getClass().getClassLoader().getResourceAsStream("addon/example2.yaml");
        var configBase = mapper.readValue(in, ConfigBase.class);
        var meta = configBase.getMeta();
        var install = configBase.getInstall();
        var spec = configBase.getSpec();

        assertEquals("django-divio", meta.getName());
        assertEquals("0.1", meta.getVersion());
        assertEquals("django==1.11.20.4", install.getPackage());
        assertEquals("0.1", spec);
    }
}