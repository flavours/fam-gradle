package com.divio.flavours.fam.gradle;

import com.divio.flavours.Utils;
import com.divio.flavours.fam.gradle.model.AddonConfig;
import com.divio.flavours.fam.gradle.model.AddonMeta;
import com.divio.flavours.fam.gradle.model.AppConfig;
import com.divio.flavours.fam.gradle.model.Meta;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;

public class Main {
    public static final String FAM_VERSION = "0.1";
    public static final String FAM_NAME = "flavour/fam-gradle";
    public static final String FAM_IDENTITY = FAM_VERSION + ":" + FAM_NAME;
    public static final File APP_FILE = Path.of("/app", "app.flavour").toFile();

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
        var appFileWasCreated = APP_FILE.createNewFile(); // creates a new file if app.flavour doesn't exist.

        AppConfig appConfig;

        if (appFileWasCreated) {
            var meta = new Meta("my-project", "0.1");
            appConfig = new AppConfig("0.1", meta, new HashMap<>());
        } else {
            appConfig = appConfigParser.parse(APP_FILE);
        }

        var writer = new StringWriter();
        addonConfigParser.write(addonConfig, writer);
        var formattedAppConfig = writer.toString();
        var addonConfigHash = Utils.toSha256String(formattedAppConfig);

        var addonAlreadyInstalled = appConfig.getAddons().entrySet().stream()
                .anyMatch(entry -> entry.getValue().getHash().equals(addonConfigHash));

        if (addonAlreadyInstalled) {
            err("Addon already installed.");
            System.exit(0);
        }

        var newAddonEntry = new AddonMeta(FAM_IDENTITY, addonConfigHash);
        var pkg = addonConfig.getInstall().getPackage();
        appConfig.getAddons().put(addonConfig.getInstall().getPackage(), newAddonEntry);
        appConfigParser.write(appConfig, APP_FILE);
        out(
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
            err("Check errors:");
            err(errors);
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
            err(
                    "Yaml parsing error:",
                    e.getMessage()
            );
            System.exit(1);
        }
        System.exit(0);
    }

    private static void printLines(final PrintStream printStream, final String... lines) {
        var message = String.join(System.lineSeparator(), lines);
        printStream.println(message);
    }

    private static void err(final String... lines) {
        printLines(System.err, lines);
    }

    private static void out(final String... lines) {
        printLines(System.out, lines);
    }

    private static void printHelp() {
        out(
                "FAM-gradle",
                "syntax: fam-gradle <add|check|remove>",
                "",
                "Data to commands should be passed through stdin."
        );
    }
}
