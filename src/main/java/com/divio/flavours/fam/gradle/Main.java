package com.divio.flavours.fam.gradle;

import com.divio.flavours.Utils;
import com.divio.flavours.fam.gradle.model.Addon;

import java.io.IOException;

public class Main {
    private final YamlParser<Addon> addonParser;

    public Main(final YamlParser<Addon> addonParser) {
        this.addonParser = addonParser;
    }

    public static void main(String[] args) throws IOException {
        var addonParser = new YamlParser<>(Addon.class);
        var app = new Main(addonParser);

        app.runArgs(args);
    }

    private void add(Addon addon) {
        throw new AssertionError("Not implemented");
    }

    private void check(Addon addon) {
        if (addon.isValid()) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }

    private Object remove(Addon addon) {
        throw new AssertionError("Not implemented");
    }

    private void runArgs(String[] args) throws IOException {
        if (args.length == 0) {
            printHelp();
            System.exit(0);
        }

        var in = Utils.readLines(System.in);

        if (in.isEmpty()) {
            printHelp();
            System.exit(0);
        }

        Addon addon = null;

        try {
            addon = addonParser.parse(in);
        } catch (YamlParseException e) {
            printLines(
                    "Error when parsing yaml:",
                    e.getMessage()
            );
            System.exit(1);
        }

        var command = args[0];

        switch (command) {
            case "add":
                add(addon);
                break;
            case "check":
                check(addon);
                break;
            case "remove":
                remove(addon);
                break;
            default:
                printHelp();
        }
    }

    private void printLines(String... lines) {
        var message = String.join(System.lineSeparator(), lines);
        System.out.println(message);
    }

    private void printHelp() {
        printLines(
                "FAM-gradle",
                "syntax: fam-gradle <add|check|remove>",
                "",
                "Data to commands should be passed through stdin."
        );
    }
}
