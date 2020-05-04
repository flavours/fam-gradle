package com.divio.flavours.fam.gradle;

import com.divio.flavours.Utils;
import com.divio.flavours.fam.gradle.model.*;
import com.divio.flavours.fam.gradle.parser.YamlParseException;
import com.divio.flavours.fam.gradle.parser.YamlParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MainIntegrationTests {
    @Test
    public void canAddAndRemoveAddonFromAppConfig() throws IOException, YamlParseException {
        var tempDirPath = Files.createTempDirectory("fam-gradle");
        var tempAddonDirectoryPath = new File(tempDirPath.toFile(), ".flavour/addons");
        tempDirPath.toFile().deleteOnExit();
        var tempAppFile = tempDirPath.resolve("app.flavour").toFile();

        var addonParser = new YamlParser<>(AddonConfig.class);
        var appParser = new YamlParser<>(AppConfig.class);
        var main = new Main(addonParser, appParser, tempAppFile, tempAddonDirectoryPath);

        // phase 1: add
        var emptyAppConfig = main.getOrCreateAppConfig();
        var addonConfig = new AddonConfig("0.1", new Install("com.divio:some-addon:1.0"), new Meta("divio/some-addon", "1.0-special"), Map.of());
        var addonConfigHash = Utils.toSha256String(addonParser.writeToString(addonConfig));
        var addAppConfig = emptyAppConfig.addAddon("divio/some-addon:1.0-special", new AddonMeta(Main.FAM_IDENTITY, addonConfigHash));

        main.add(addonConfig, emptyAppConfig);
        assertThat(tempAppFile).hasContent(appParser.writeToString(addAppConfig));
        assertThat(new File(tempAddonDirectoryPath, addonConfigHash)).exists();

        // phase 2: remove
        emptyAppConfig = main.getOrCreateAppConfig();
        var writtenAppConfig = appParser.parse(tempAppFile);
        var afterRemoval = writtenAppConfig.removeAddon(addonConfig);
        main.remove(addonConfig, emptyAppConfig);
        assertThat(tempAppFile).hasContent(appParser.writeToString(afterRemoval));
        assertThat(new File(tempAddonDirectoryPath, addonConfigHash)).doesNotExist();
    }
}
