package me.ikevoodoo.smpcore.utils;

import org.bukkit.Bukkit;

public class ServerUtils {

    private ServerUtils() {

    }

    public static int getMajorVersion() {
        // Get version, excluding snapshot
        String version = Bukkit.getVersion().replaceAll("-SNAPSHOT.*", "");
        int dot = version.indexOf('.');
        if (dot != version.indexOf('.')) version = version.substring(0, dot);

        return Integer.parseInt(version.substring(2));
    }

    public static boolean hasRgbSupport() {
        return getMajorVersion() >= 16;
    }

}
