package com.divio.flavours;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Utils {
    public static byte[] toSha256Digest(String text) {
        try {
            var digester = MessageDigest.getInstance("SHA-256");
            digester.update(text.getBytes(StandardCharsets.UTF_8));
            var digest = digester.digest();
            return digest;
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("SHA-256 MessageDigest not available.", nsae);
        }
    }

    public static String toHexString(final byte[] bytes) {
        var sb = new StringBuffer();
        for (var b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static String toSha256String(final String text) {
        return toHexString(toSha256Digest(text));
    }

    public static List<String> readLines(InputStream in) throws IOException {
        if (in.available() == 0)
            return Collections.emptyList();

        try (var bin = new BufferedReader(new InputStreamReader(in))) {
            return bin.lines().collect(Collectors.toList());
        }
    }

    public static void printLines(final PrintStream printStream, final String... lines) {
        var message = String.join(System.lineSeparator(), lines);
        printStream.println(message);
    }
}
