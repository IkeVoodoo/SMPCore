package me.ikevoodoo.smpcore.commands.arguments;

public record Argument(String name, boolean required, Class<?> type, OptionalFor optionalFor) {
}
