package com.divio.flavours;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UtilsTest {
    @Test
    public void testToHexStringWithDigest() {
        var expected = "0a0a9f2a6772942557ab5355d76af442f8f65e01";
        var actual = Utils.toSha1String("Hello, World!");
        assertEquals(expected, actual);
    }
}
