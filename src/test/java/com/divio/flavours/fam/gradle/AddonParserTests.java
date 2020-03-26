package com.divio.flavours.fam.gradle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.junit.jupiter.api.Test;

import javax.validation.Validation;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class AddonParserTests {
    private List<String> readResource(final String resourceName) throws IOException {
        var in = getClass().getClassLoader().getResourceAsStream(resourceName);
        assertThat(in).isNotNull();
        try (var bin = new BufferedReader(new InputStreamReader(in))) {
            return bin.lines().collect(Collectors.toList());
        }
    }

    @Test
    public void canParseConfigBase() throws IOException, AddonParseException {
        var mapper = new ObjectMapper(new YAMLFactory());
        var validatorFactory = Validation.buildDefaultValidatorFactory();
        var addonParser = new AddonParser(mapper, validatorFactory);

        var addon = addonParser.parse(readResource("addon/example1.yaml"));
        var meta = addon.getMeta();
        var install = addon.getInstall();
        var spec = addon.getSpec();

        assertThat(meta.getName()).isEqualTo("django-divio");
        assertThat(meta.getVersion()).isEqualTo("0.1");
        assertThat(install.getPackage()).isEqualTo("django==1.11.20.4");
        assertThat(spec).isEqualTo("0.1");

        var config = addon.getConfig();
        var languageConfig = config.get("languages");

        assertThat(languageConfig.getLabel()).isEqualTo("Languages");
        assertThat(languageConfig.isRequired()).isTrue();
        assertThat(languageConfig.getType()).isEqualTo("scalar/string");
        assertThat(languageConfig.getDefault()).isEqualTo("en,de");
        assertThat(languageConfig.getHelpText()).isEqualTo("WARNING: this field is auto-written. Please do not change it here.");
    }

    @Test
    public void failsOnIncompleteYaml() throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory());
        var validatorFactory = Validation.buildDefaultValidatorFactory();
        var addonParser = new AddonParser(mapper, validatorFactory);
        var addonConfigLines = readResource("addon/error-example.yaml");

        assertThatThrownBy(() -> addonParser.parse(addonConfigLines))
                .isInstanceOf(AddonParseException.class)
                .hasMessageContaining("installValue must not be null")
                .hasMessageContaining("metaValue.nameValue must not be blank");
    }
}
