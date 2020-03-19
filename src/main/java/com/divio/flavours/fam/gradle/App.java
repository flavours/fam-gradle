package com.divio.flavours.fam.gradle;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.divio.flavours.addon.model.Addon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class App {

    public Object add() throws IOException {
        var inputLines = readLines(System.in);
        
        return null;
    }

    public Object check() throws IOException {
        var inputLines = readLines(System.in);
        
        return null;
    }

    public Object remove(String addonName) {
        return null;
    }

    public List<String> readLines(InputStream in) throws IOException {
        if (in.available() == 0)
            return Collections.emptyList();

        try (var bin = new BufferedReader(new InputStreamReader(in))) {
            var result = bin.lines().collect(Collectors.toList());
            return result;
        }
    }

    public Addon linesToYaml(List<String> lines) throws JsonMappingException, JsonProcessingException {
        var mapper = new ObjectMapper(new YAMLFactory());
        var joined = String.join("\n", lines);
        var addon = mapper.readValue(joined, Addon.class);
        return addon;
    }

    private void runArgs(String[] args) throws IOException {
        if (args.length == 0) {
            printHelpDefault();
            return;
        }

        var first = args[0];

        switch (first) {
            case "add":
                break;
            case "check":
                break;
            case "remove":
                if (args.length != 2) {
                    printHelpRemove();
                    return;
                }

                var addonName = args[1];
                remove(addonName);
                break;
            default:
                printHelpDefault();
        }
    }

    private void printHelpMessage(String ...lines) {
        var message = String.join(System.lineSeparator(), lines);
        System.out.println(message);
    }

    private void printHelpDefault() {
        var lines = new String[] {
            "",
            "",
            "",
            "",
        };

        printHelpMessage(lines);
    }

    private void printHelpRemove() {
        printHelpMessage();
    }

    public static void main(String[] args) throws IOException {
        var app = new App();
        app.runArgs(args);
        // var lines = app.readLines(System.in);

        // for (var line : lines) {
        //     System.out.println("Line: " + line);
        // }
    }
}
