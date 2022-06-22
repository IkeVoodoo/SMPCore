package me.ikevoodoo.smpcore.utils;

import java.io.File;

public class FileUtils {

    private FileUtils() {

    }

    public static boolean delete(File file) {
        boolean result = true;
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                result = false;
            } else {
                for (File f : files) {
                    if (!delete(f))
                        result = false;
                }
            }
        }
        if (!file.delete())
            result = false;
        return result;
    }

}
