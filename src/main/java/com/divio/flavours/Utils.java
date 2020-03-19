package com.divio.flavours;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Utils {
    public static byte[] toSha1Digest(String text) {
        try {
            var digester = MessageDigest.getInstance("SHA-1");
            digester.update(text.getBytes("UTF-8"));
            var digest = digester.digest();
            return digest;
        }
        catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("SHA-1 MessageDigest not available.", nsae);
        }
        catch (UnsupportedEncodingException uee) {
            throw new RuntimeException("UTF-8 not supported.", uee);
        }
    }

    public static String toHexString(final byte[] bytes) {
        var sb = new StringBuffer();
        for (var b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }

    public static String toSha1String(final String text) {
        return toHexString(toSha1Digest(text));
    }
}