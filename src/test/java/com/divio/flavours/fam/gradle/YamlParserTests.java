package com.divio.flavours.fam.gradle;

import com.divio.flavours.fam.gradle.model.AddonConfig;
import com.divio.flavours.fam.gradle.model.AddonMeta;
import com.divio.flavours.fam.gradle.model.AppConfig;
import com.divio.flavours.fam.gradle.parser.YamlParseException;
import com.divio.flavours.fam.gradle.parser.YamlParser;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class YamlParserTests {
    private List<String> readResource(final String resourceName) throws IOException {
        var in = getClass().getClassLoader().getResourceAsStream(resourceName);
        assertThat(in).isNotNull();
        try (var bin = new BufferedReader(new InputStreamReader(in))) {
            return bin.lines().collect(Collectors.toList());
        }
    }

    @Test
    public void canParseAddonConfig() throws IOException, YamlParseException {
        var addonParser = new YamlParser<>(AddonConfig.class);

        var addon = addonParser.parse(readResource("addon/example1.yaml"));

        assertThat(addon.getSpec()).isEqualTo("0.1");
        assertThat(addon.getMeta().getName()).isEqualTo("django-divio");
        assertThat(addon.getMeta().getVersion()).isEqualTo("0.1");
        assertThat(addon.getInstall().getPackage()).isEqualTo("django==1.11.20.4");

        var config = addon.getConfig();
        var languageConfig = config.get("languages");

        assertThat(languageConfig.getLabel()).isEqualTo("Languages");
        assertThat(languageConfig.isRequired()).isTrue();
        assertThat(languageConfig.getType()).isEqualTo("scalar/string");
        assertThat(languageConfig.getDefault()).isEqualTo("en,de");
        assertThat(languageConfig.getHelpText()).isEqualTo("WARNING: this field is auto-written. Please do not change it here.");
    }

    @Test
    public void canParseAppConfig() throws IOException, YamlParseException {
        var appParser = new YamlParser<>(AppConfig.class);

        var app = appParser.parse(readResource("app/app.flavour"));

        assertThat(app.getSpec()).isEqualTo("0.1");
        assertThat(app.getMeta().getName()).isEqualTo("my-aldryn-project");
        assertThat(app.getMeta().getVersion()).isEqualTo("0.1");

        var addons = app.getAddons();
        assertThat(addons.get("addon/aldryn-addons:1.0.4").getManager()).isEqualTo("flavour/fam-aldryn:0.1");
        assertThat(addons.get("addon/aldryn-addons:1.0.4").getHash()).isEqualTo("1cf06ba56949fe7370d81b9ba459a272cf1879036d9a363a119cd441d8854182");
        assertThat(addons.get("addon/aldryn-common:1.0.4").getManager()).isEqualTo("flavour/fam-aldryn:0.1");
        assertThat(addons.get("addon/aldryn-common:1.0.4").getHash()).isEqualTo("f2c5818177ea75546d2e18d65f2d6890ddfa7d87fc617d7200c9df7c2f9857f2");
    }

    @Test
    public void failsOnIncompleteYaml() throws IOException {
        var addonParser = new YamlParser<>(AddonConfig.class);
        var addonConfigLines = readResource("addon/error-example.yaml");

        assertThatThrownBy(() -> addonParser.parse(addonConfigLines))
                .isInstanceOf(YamlParseException.class)
                .hasMessageContaining("installValue must not be null")
                .hasMessageContaining("metaValue.nameValue must not be blank");
    }

    @Test
    public void canWriteAnAST() throws IOException, YamlParseException {
        var appParser = new YamlParser<>(AppConfig.class);
        var app = appParser.parse(readResource("app/app.flavour"));
        app.getAddons().put("addon/s3-addons:1.0.0", new AddonMeta("flavour/fam-gradle:0.1", "abc123"));

        var writer = new StringWriter();
        appParser.write(app, writer);
        var newData = writer.toString();
        var updatedApp = appParser.parse(newData);
        assertThat(updatedApp.getAddons().get("addon/s3-addons:1.0.0").getManager()).isEqualTo("flavour/fam-gradle:0.1");
        assertThat(updatedApp.getAddons().get("addon/s3-addons:1.0.0").getHash()).isEqualTo("abc123");
    }
}
