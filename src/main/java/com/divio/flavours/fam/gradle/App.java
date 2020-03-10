package com.divio.flavours.fam.gradle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class App {
    public String getGreeting() {
        return "Hello world.";
    }

    public List<String> readLines(InputStream in) throws IOException {
        if (in.available() == 0)
            return Collections.emptyList();

        try (var bin = new BufferedReader(new InputStreamReader(in))) {
            var result = bin.lines().collect(Collectors.toList());
            return result;
        }
    }

    public void linesToYaml(List<String> lines) {
        var mapper = new ObjectMapper(new YAMLFactory());
        var sb = new StringBuilder();
        for (var line : lines) sb.append(line);
    }

    public static void main(String[] args) throws IOException {
        var app = new App();
        var lines = app.readLines(System.in);

        for (var line : lines) {
            System.out.println("Line: " + line);
        }
    }
}
