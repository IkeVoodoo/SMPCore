package me.ikevoodoo.smpcore.utils;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StringUtils {

    private StringUtils() {

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
        var time = DateTimeFormatter.ofPattern("HH:mm:ss.SSSS").parse(timeString).query(TemporalQueries.localTime());
        // time's hours in milliseconds
        // time's minutes in milliseconds
        // time's seconds in milliseconds
        // time's milliseconds
        // time's nanoseconds in milliseconds
        long hours = time.getHour()                               * 60L * 60L * 1000L;
        long minutes = time.getMinute()                           * 60L * 1000L;
        long seconds = time.getSecond()                           * 1000L;
        long milliseconds = time.get(ChronoField.MILLI_OF_SECOND) * 1000L;
        return hours + minutes + seconds + milliseconds;
    }

}
