package me.ikevoodoo.smpcore.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class StreamUtils {

    public static OutputStream gzip(OutputStream stream) throws IOException {
        return new GZIPOutputStream(stream);
    }

    public static InputStream gzip(InputStream stream) throws IOException {
        return new GZIPInputStream(stream);
    }

    public static void readAndDump(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}
