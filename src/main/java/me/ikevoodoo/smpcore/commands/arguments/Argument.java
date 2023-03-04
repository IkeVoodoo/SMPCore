package me.ikevoodoo.smpcore.commands.arguments;

import me.ikevoodoo.smpcore.commands.Context;

import java.util.List;
import java.util.function.Function;

public record Argument(String name, boolean required, Class<?> type, OptionalFor optionalFor, Function<Context<?>, List<String>> tabCompletion, boolean cacheCompletion) {
    private static final List<String> EMPTY_COMPLETIONS = List.of();
    private static final Function<Context<?>, List<String>> EMPTY_COMPLETER = ctx -> EMPTY_COMPLETIONS;

    public Argument(String name, boolean required, Class<?> type, OptionalFor optionalFor) {
        this(name, required, type, optionalFor, EMPTY_COMPLETER, true);
    }

    public Argument(String name, boolean required, Class<?> type, OptionalFor optionalFor, Function<Context<?>, List<String>> tabCompletion, boolean cacheCompletion) {
        this.name = name;
        this.required = required;
        this.type = type;
        this.optionalFor = optionalFor;
        this.tabCompletion = tabCompletion == null ? EMPTY_COMPLETER : tabCompletion;
        this.cacheCompletion = cacheCompletion;
    }
}
