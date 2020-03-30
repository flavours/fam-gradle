package com.divio.flavours.fam.gradle;

import com.divio.flavours.Utils;
import com.divio.flavours.fam.gradle.model.AddonConfig;
import com.divio.flavours.fam.gradle.model.AddonMeta;
import com.divio.flavours.fam.gradle.model.AppConfig;
import com.divio.flavours.fam.gradle.model.Meta;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static com.divio.flavours.Utils.printLines;

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

    private void add(final AddonConfig addonConfig, final AppConfig appConfig) throws IOException {
        var writer = new StringWriter();
        addonConfigParser.write(addonConfig, writer);
        var formattedAppConfig = writer.toString();
        var addonConfigHash = Utils.toSha256String(formattedAppConfig);

        var addonAlreadyInstalled = appConfig.getAddons().entrySet().stream()
                .anyMatch(entry -> entry.getValue().getHash().equals(addonConfigHash));

        if (addonAlreadyInstalled) {
            printLines(System.err, "Addon already installed.");
            return;
        }

        var newAddonEntry = new AddonMeta(FAM_IDENTITY, addonConfigHash);
        var pkg = addonConfig.getInstall().getPackage();
        appConfig.getAddons().put(addonConfig.getInstall().getPackage(), newAddonEntry);
        appConfigParser.write(appConfig, APP_FILE);
        printLines(System.out,
                "Added addon",
                "Package: " + pkg,
                "Hash:    " + addonConfigHash
        );
    }

    private void check(AddonConfig addon) {
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

    private void remove(final AddonConfig addon, final AppConfig appConfig) throws IOException {
        var formattedAddonConfig = addonConfigParser.writeToString(addon);
        var addonConfigHash = Utils.toSha256String(formattedAddonConfig);
        var filteredAddons = appConfig.getAddons().entrySet().stream()
                .filter(entrySet -> !entrySet.getValue().getHash().equals(addonConfigHash))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        var newAppConfig = new AppConfig(appConfig.getSpec(), appConfig.getMeta(), filteredAddons);
        appConfigParser.write(newAppConfig, APP_FILE);
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

    private AppConfig getOrCreateAppConfig() throws YamlParseException, IOException {
        boolean appFileWasCreated = false;
        try {
            appFileWasCreated = APP_FILE.createNewFile();
        } catch (IOException e) {
            printLines(System.err,
                    "Could not access file '" + APP_FILE.getAbsolutePath() + "'."
            );
            System.exit(1);
        }

        AppConfig appConfig;

        if (appFileWasCreated) {
            var meta = new Meta("my-project", "0.1");
            appConfig = new AppConfig("0.1", meta, new HashMap<>());
        } else {
            appConfig = appConfigParser.parse(APP_FILE);
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
