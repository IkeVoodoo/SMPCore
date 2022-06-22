package me.ikevoodoo.smpcore.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class ExceptionUtils {

    private ExceptionUtils() {

    }

    public static String asString(Exception e) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out, false, StandardCharsets.UTF_8);
        e.printStackTrace(ps);
        String text = out.toString(StandardCharsets.UTF_8);
        ps.close();
        return text;
    }

}
