package me.ikevoodoo.smpcore.text.messaging;

public enum MessageProperty {

    BOLD,
    ITALIC,
    STRIKETHROUGH,
    UNDERLINE,
    OBFUSCATED,
    RESET;

    public static MessageProperty fromString(String s) {
        if (s == null) {
            return null;
        }
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

}
