package me.ikevoodoo.smpcore.utils;

import org.bukkit.ChatColor;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StringUtils {

    private StringUtils() {

    }

    public static String lowercaseFirst(String s) {
        if (s.isBlank()) return s;

        if (s.length() == 1) return s.toLowerCase(Locale.ROOT);

        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    public static String color(String s) {
        if (s == null) return null;
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private static int distance(String a, String b) {
        int len1 = a.length();
        int len2 = b.length();
        int[][] dp = new int[len1 + 1][len2 + 1];

        for (int i = 0; i <= len1; i++) {
            dp[i][0] = i;
        }

        for (int j = 0; j <= len2; j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                int cost = a.charAt(i - 1) == b.charAt(j - 1) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }

        return dp[len1][len2];
    }

    public static String getClosest(String input, String[] options) {
        List<Pair<Integer, String>> distances = new ArrayList<>();

        for(String s : options) {
            distances.add(new Pair<>(distance(input.toUpperCase(Locale.ROOT), s.toUpperCase(Locale.ROOT)), s));
        }

        return distances.stream()
                .sorted()
                .findFirst()
                .orElseGet(() -> new Pair<>(Integer.MAX_VALUE, null))
                .getSecond();
    }

    public static String[] toStringArray(Enum<?>[] enums) {
        String[] strings = new String[enums.length];
        for (int i = 0; i < enums.length; i++) {
            strings[i] = enums[i].name();
        }
        return strings;
    }

    public static String toEnumCompatible(String input) {
        return input.toUpperCase(Locale.ROOT)
                .replaceAll("([a-zA-Z0-9])\\s+", "$1_")
                .replaceAll("_+", "_")
                .replaceAll("-+", "-")
                .replace("MINECRAFT:", "")
                .replace(":", "_")
                .replaceAll("^\\s+", "")
                .replaceAll("\\s+$", "");
    }

    public static String removeTrailingZeros(String string) {
        return string.contains(".")
                ? string.replaceAll("0*$", "").replaceAll("\\.$", "")
                : string;
    }

    public static String slice(String input, int start, int end) {
        int endIndex = end < 0 ? input.length() + end : end;
        if(endIndex > input.length())
            endIndex = input.length();
        return input.substring(start > input.length() ? start % input.length() : start, endIndex);
    }

    public static String sliceFrom(String input, int start) {
        return slice(input, start, Integer.MAX_VALUE);
    }

    public static String sliceTo(String input, int end) {
        return slice(input, 0, end);
    }

    public static long parseBanTime(String timeString) {
        return DateTimeFormatter
                .ofPattern("HH:mm:ss.SSSS", Locale.ROOT)
                .parse(timeString, TemporalQueries.localTime())
                .getLong(ChronoField.MILLI_OF_DAY);
    }

    public static String formatTime(long end) {
        if (end == Long.MAX_VALUE)
            return "Infinity";

        long time = end - System.currentTimeMillis();

        long seconds = (time / 1000) % 60;
        long minutes = (time / (1000 * 60)) % 60;
        long hours = (time / (1000 * 60 * 60)) % 24;

        StringBuilder stringBuilder = new StringBuilder();
        if (hours > 0) {
            stringBuilder.append(fixedNumberLength(hours, 2)).append(" hours, ");
            if (minutes == 0) {
                stringBuilder.append("00 minutes, ");
                if (seconds == 0) {
                    stringBuilder.append("00 seconds");
                }
            }
        }

        if (minutes > 0) {
            stringBuilder.append(fixedNumberLength(minutes, 2)).append(" minutes, ");
            if (seconds == 0) {
                stringBuilder.append("00 seconds");
            }
        }

        if (seconds > 0)
            stringBuilder.append(fixedNumberLength(seconds, 2)).append(" seconds");

        if (stringBuilder.length() == 0)
            stringBuilder.append("now");
        return stringBuilder.toString();
    }

    public static String fixedNumberLength(long number, int length) {
        String numString = String.valueOf(number);
        if (numString.length() < length) {
            return "0".repeat(length - numString.length()) + numString;
        }
        return numString;
    }

    // https://stackoverflow.com/a/332101/13050697
    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();

        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public static byte[] fromHexString(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return data;
    }

    public static String stripExtension(String name) {
        var extensionSeparator = name.lastIndexOf('.');
        if (extensionSeparator > 0) {
            return name.substring(0, extensionSeparator);
        }

        return name;
    }

    public static String getExtension(String name) {
        var extensionSeparator = name.lastIndexOf('.');
        if (extensionSeparator < name.length() - 1) {
            return name.substring(extensionSeparator + 1);
        }

        return name;
    }

}
