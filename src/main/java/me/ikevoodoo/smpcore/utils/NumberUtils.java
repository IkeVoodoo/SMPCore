package me.ikevoodoo.smpcore.utils;

public class NumberUtils {

    private NumberUtils() {

    }

    public static byte[] toByteArray(int i) {
        return new byte[] {
            (byte) (i >> 24),
            (byte) (i >> 16),
            (byte) (i >> 8),
            (byte) i
        };
    }

    public static byte[] toByteArray(long l) {
        return new byte[] {
            (byte) (l >> 56),
            (byte) (l >> 48),
            (byte) (l >> 40),
            (byte) (l >> 32),
            (byte) (l >> 24),
            (byte) (l >> 16),
            (byte) (l >> 8),
            (byte) l
        };
    }

    public static byte[] toByteArray(short s) {
        return new byte[] {
            (byte) (s >> 8),
            (byte) s
        };
    }
    public static int toInt(byte[] bytes) {
        return (bytes[0] << 24) + (bytes[1] << 16) + (bytes[2] << 8) + bytes[3];
    }

    public static long toLong(byte[] bytes) {
        return ((long) bytes[0] << 56) + ((long) bytes[1] << 48) + ((long) bytes[2] << 40) + ((long) bytes[3] << 32) + ((long) bytes[4] << 24) + ((long) bytes[5] << 16) + ((long) bytes[6] << 8) + bytes[7];
    }

    public static short toShort(byte[] bytes) {
        return (short) ((bytes[0] << 8) + bytes[1]);
    }

    public static byte[] toByteArray(Number number) {
        if (number instanceof Integer) {
            return toByteArray((int) number);
        } else if (number instanceof Long) {
            return toByteArray((long) number);
        } else if (number instanceof Short) {
            return toByteArray((short) number);
        } else {
            throw new IllegalArgumentException("Number must be one of the following: Integer, Long, Short, Double, Float");
        }
    }

    public static Number fromArray(byte[] bytes) {
        if (bytes.length == 4) {
            return toInt(bytes);
        }

        if (bytes.length == 8) {
            return toLong(bytes);
        }

        if (bytes.length == 2) {
            return toShort(bytes);
        }

        throw new IllegalArgumentException("Number must be one of the following: Integer, Long, Short, Double, Float");
    }



}
