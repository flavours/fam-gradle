package com.divio.flavours.fam.gradle;

import com.divio.flavours.Utils;
import com.divio.flavours.fam.gradle.model.*;
import com.divio.flavours.fam.gradle.parser.YamlParseException;
import com.divio.flavours.fam.gradle.parser.YamlParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MainIntegrationTests {
    @Test
    public void canAddAndRemoveAddonFromAppConfig() throws IOException, YamlParseException {
        var tempDirPath = Files.createTempDirectory("fam-gradle");
        tempDirPath.toFile().deleteOnExit();
        var tempAppFile = tempDirPath.resolve("app.flavour").toFile();

        var addonParser = new YamlParser<>(AddonConfig.class);
        var appParser = new YamlParser<>(AppConfig.class);
        var main = new Main(addonParser, appParser, tempAppFile);

        var addonConfig = new AddonConfig("0.1", new Install("com.divio:some-addon:1.0"), new Meta("some-addon", "1.0"), Map.of());
        var addonConfigHash = Utils.toSha256String(addonParser.writeToString(addonConfig));
        var emptyAppConfig = main.getOrCreateAppConfig();
        var addAppConfig = emptyAppConfig.addAddon("com.divio:some-addon:1.0", new AddonMeta(Main.FAM_IDENTITY, addonConfigHash));

        main.add(addonConfig, emptyAppConfig);
        assertThat(tempAppFile).hasContent(appParser.writeToString(addAppConfig));

        var writtenAppConfig = appParser.parse(tempAppFile);
        var afterRemoval = writtenAppConfig.removeAddon(addonConfigHash);
        main.remove(addonConfig, emptyAppConfig);
        assertThat(tempAppFile).hasContent(appParser.writeToString(afterRemoval));
    }
}
