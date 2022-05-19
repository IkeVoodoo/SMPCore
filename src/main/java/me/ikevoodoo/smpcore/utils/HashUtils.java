package me.ikevoodoo.smpcore.utils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {

    private HashUtils() {
    }

    public static byte[] sha1Hash(InputStream inputStream) throws NoSuchAlgorithmException, IOException {
        byte[] buffer = new byte[8192];
        int read;
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        try(BufferedInputStream bis = new BufferedInputStream(inputStream)) {
            while ((read = bis.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
        }
        return digest.digest();
    }

}
