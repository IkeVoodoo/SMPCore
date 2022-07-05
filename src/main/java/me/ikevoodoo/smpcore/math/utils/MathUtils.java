package me.ikevoodoo.smpcore.math.utils;

public class MathUtils {

    private MathUtils() {

    }

    public static double percentageOf(double percentage, double value) {
        return (percentage / 100) * value;
    }

    public static double whatPercentage(double num, double value) {
        return (num / value) * 100;
    }

    public static double percentageOfWhat(double num, double value) {
        return (value / num) * 100;
    }
}
