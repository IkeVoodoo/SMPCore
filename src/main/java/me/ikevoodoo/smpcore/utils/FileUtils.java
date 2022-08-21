package me.ikevoodoo.smpcore.utils;

import java.io.File;
import java.io.IOException;

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

    public static File get(File file, String... paths) {
        return new File(file, String.join(File.separator, paths));
    }

    public static File getOrCreate(File file, String... paths) throws IOException {
        File result = get(file, paths);
        if (!result.exists()) {
            result.getParentFile().mkdirs();
            result.createNewFile();
        }
        return result;
    }

}
