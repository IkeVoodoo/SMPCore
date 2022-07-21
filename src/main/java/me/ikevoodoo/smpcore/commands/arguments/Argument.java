package me.ikevoodoo.smpcore.commands.arguments;

import me.ikevoodoo.smpcore.commands.Context;

import java.util.List;
import java.util.function.Function;

public record Argument(String name, boolean required, Class<?> type, OptionalFor optionalFor, Function<Context<?>, List<String>> tabCompletion, boolean cacheCompletion) {
    public Argument(String name, boolean required, Class<?> type, OptionalFor optionalFor) {
        this(name, required, type, optionalFor, null, true);
    }
}
