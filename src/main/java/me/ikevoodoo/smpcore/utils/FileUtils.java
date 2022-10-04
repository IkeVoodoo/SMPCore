package me.ikevoodoo.smpcore.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    public static void addToZip(File folder, ZipOutputStream os) {
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                addToZip(file, os);
                continue;
            }
            ZipEntry entry = new ZipEntry(folder.toPath().relativize(file.toPath()).toString());
            try {
                os.putNextEntry(entry);
                Files.copy(file.toPath(), os);
                os.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getName(File file) {
        String name = file.getName();
        int i = name.lastIndexOf('.');
        if (i > 0) {
            return name.substring(0, i);
        }
        return name;
    }

}
