package com.divio.flavours.fam.gradle;

import com.divio.flavours.Utils;
import com.divio.flavours.fam.gradle.model.*;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

public class Main {
    public static final String FAM_VERSION = "0.1";
    public static final String FAM_NAME = "flavour/fam-gradle";
    public static final String FAM_IDENTITY = FAM_VERSION + ":" + FAM_NAME;

    private final YamlParser<AddonConfig> addonConfigParser;
    private final YamlParser<AppConfig> appConfigParser;

    public Main(final YamlParser<AddonConfig> addonConfigParser, final YamlParser<AppConfig> appConfigParser) {
        this.addonConfigParser = addonConfigParser;
        this.appConfigParser = appConfigParser;
    }

    public static void main(String[] args) throws IOException {
        var addonConfigParser = new YamlParser<>(AddonConfig.class);
        var appConfigParser = new YamlParser<>(AppConfig.class);
        var app = new Main(addonConfigParser, appConfigParser);

        app.runArgs(args);
    }

    private void add(AddonConfig addonConfig) throws IOException, YamlParseException {
        var appFile = new File("app.flavour");
        var appFileExists = appFile.createNewFile(); // creates a new file if app.flavour doesn't exist.

        AppConfig appConfig;

        if (appFileExists) {
            appConfig = appConfigParser.parse(appFile);
        } else {
            var meta = new Meta("my-project", "0.1");
            appConfig = new AppConfig("0.1", meta, new HashMap<>());
        }

        var writer = new StringWriter();
        addonConfigParser.write(addonConfig, writer);
        var formattedAppConfig = writer.toString();
        var addonConfigHash = Utils.toSha256String(formattedAppConfig);

        var addonAlreadyInstalled = appConfig.getAddons().entrySet().stream()
                .anyMatch(entry -> entry.getValue().getHash().equals(addonConfigHash));

        if (addonAlreadyInstalled) {
            printLines("Addon already installed.");
            System.exit(0);
        }

        var newAddonEntry = new AddonMeta(FAM_IDENTITY, addonConfigHash);
        var pkg = addonConfig.getInstall().getPackage();
        appConfig.getAddons().put(addonConfig.getInstall().getPackage(), newAddonEntry);
        appConfigParser.write(appConfig, appFile);
        printLines(
                "Added addon",
                "Package: " + pkg,
                "Hash:    " + addonConfigHash
        );
        System.exit(0);
    }

    private void check(AddonConfig addon) {
        var constraintViolations = addon.validate();
        if (constraintViolations.isEmpty()) {
            System.exit(0);
        } else {
            var errors = (String[]) constraintViolations.stream()
                    .map(cv -> cv.getPropertyPath().toString() + " " + cv.getMessage())
                    .toArray();
            printLines("Check errors:");
            printLines(errors);
            System.exit(1);
        }
    }

    private Object remove(AddonConfig addon) {
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

        var command = args[0];

        try {
            var addonConfig = addonConfigParser.parse(in);

            switch (command) {
                case "add":
                    add(addonConfig);
                    break;
                case "check":
                    check(addonConfig);
                    break;
                case "remove":
                    remove(addonConfig);
                    break;
                default:
                    printHelp();
            }
        } catch (YamlParseException e) {
            printLines(
                    "Yaml parsing error:",
                    e.getMessage()
            );
            System.exit(1);
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
