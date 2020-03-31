package com.divio.flavours.fam.gradle;

import com.divio.flavours.Utils;
import com.divio.flavours.fam.gradle.model.AddonConfig;
import com.divio.flavours.fam.gradle.model.AddonMeta;
import com.divio.flavours.fam.gradle.model.AppConfig;
import com.divio.flavours.fam.gradle.model.Meta;
import com.divio.flavours.fam.gradle.parser.YamlParseException;
import com.divio.flavours.fam.gradle.parser.YamlParser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

import static com.divio.flavours.Utils.printLines;

public class Main {
    public static final String FAM_NAME = "flavour/fam-gradle";
    public static final String FAM_VERSION = "0.1";
    public static final String FAM_IDENTITY = FAM_NAME + ":" + FAM_VERSION;
    public static final File RUNTIME_APP_FILE = Path.of("/app", "app.flavour").toFile();

    private final YamlParser<AddonConfig> addonConfigParser;
    private final YamlParser<AppConfig> appConfigParser;
    private final File appFile;

    public Main(
            final YamlParser<AddonConfig> addonConfigParser,
            final YamlParser<AppConfig> appConfigParser,
            final File appFile
    ) {
        this.addonConfigParser = addonConfigParser;
        this.appConfigParser = appConfigParser;
        this.appFile = appFile;
    }

    public Main(final File appFile) {
        this(new YamlParser<>(AddonConfig.class), new YamlParser<>(AppConfig.class), appFile);
    }

    public static void main(String[] args) throws IOException {
        var app = new Main(RUNTIME_APP_FILE);
        app.runArgs(args);
    }

    void add(final AddonConfig addonConfig, final AppConfig appConfig) throws IOException {
        var formattedAppConfig = addonConfigParser.writeToString(addonConfig);
        var addonConfigHash = Utils.toSha256String(formattedAppConfig);
        if (appConfig.hasAddon(addonConfigHash)) {
            printLines(System.err, "Addon already installed.");
            return;
        }

        var packageValue = addonConfig.getInstall().getPackage();
        var newAppConfig = appConfig.addAddon(packageValue, new AddonMeta(FAM_IDENTITY, addonConfigHash));

        appConfigParser.write(newAppConfig, appFile);

        printLines(System.out,
                "Added addon",
                "Package: " + packageValue,
                "Hash:    " + addonConfigHash
        );
    }

    void check(AddonConfig addon) {
        var constraintViolations = addon.validate();
        if (!constraintViolations.isEmpty()) {
            var errors = (String[]) constraintViolations.stream()
                    .map(cv -> cv.getPropertyPath().toString() + " " + cv.getMessage())
                    .toArray();
            printLines(System.err, "Check errors:");
            printLines(System.err, errors);
            System.exit(1);
        }
    }

    void remove(final AddonConfig addon, final AppConfig appConfig) throws IOException {
        var formattedAddonConfig = addonConfigParser.writeToString(addon);
        var addonConfigHash = Utils.toSha256String(formattedAddonConfig);
        var newAppConfig = appConfig.removeAddon(addonConfigHash);

        appConfigParser.write(newAppConfig, appFile);
        printLines(System.out,
                "Removed addon",
                "Package: " + addon.getInstall().getPackage(),
                "Hash:    " + addonConfigHash
        );
    }

    private void runArgs(String[] args) throws IOException {
        if (args.length == 0) {
            printHelp();
            return;
        }

        var in = Utils.readLines(System.in);

        if (in.isEmpty()) {
            printHelp();
            return;
        }

        var command = args[0];

        try {
            var addonConfig = addonConfigParser.parse(in);

            switch (command) {
                case "add":
                    add(addonConfig, getOrCreateAppConfig());
                    break;
                case "check":
                    check(addonConfig);
                    break;
                case "remove":
                    remove(addonConfig, getOrCreateAppConfig());
                    break;
                default:
                    printHelp();
            }
        } catch (YamlParseException e) {
            printLines(System.err,
                    "Yaml parsing error:",
                    e.getMessage()
            );
            System.exit(1);
        }
    }

    AppConfig getOrCreateAppConfig() throws YamlParseException, IOException {
        boolean appFileWasCreated = false;
        try {
            appFileWasCreated = appFile.createNewFile();
        } catch (IOException e) {
            printLines(System.err,
                    "Could not access file '" + appFile.getAbsolutePath() + "'."
            );
            System.exit(1);
        }

        AppConfig appConfig;

        if (appFileWasCreated) {
            var meta = new Meta("my-project", "0.1");
            appConfig = new AppConfig("0.1", meta, new HashMap<>());
        } else {
            appConfig = appConfigParser.parse(appFile);
        }

        return appConfig;
    }

    private static void printHelp() {
        printLines(System.out,
                "FAM-gradle",
                "syntax: fam-gradle <add|check|remove>",
                "",
                "Data to commands should be passed through stdin."
        );
    }
}
