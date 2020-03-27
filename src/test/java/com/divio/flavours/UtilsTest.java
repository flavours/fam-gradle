package com.divio.flavours;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UtilsTest {
    @Test
    public void testToHexStringWithDigest() {
        var expected = "dffd6021bb2bd5b0af676290809ec3a53191dd81c7f70a4b28688a362182986f";
        var actual = Utils.toSha256String("Hello, World!");
        assertThat(actual).isEqualTo(expected);
    }
}
