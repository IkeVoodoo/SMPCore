package me.ikevoodoo.smpcore.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class NetworkUtils {

    private NetworkUtils() {

    }

    public static String getServerIp() {
        return URLUtils.ifValid("http://checkip.amazonaws.com/", connection -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                return reader.readLine();
            }
        });
    }

}
