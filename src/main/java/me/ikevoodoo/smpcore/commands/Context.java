package me.ikevoodoo.smpcore.commands;

import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import org.bukkit.command.CommandSender;

public class Context<T extends CommandSender> {

    private final T source;
    private final Arguments args;

    protected Context(T source, Arguments args) {
        this.source = source;
        this.args = args;
    }

    public Arguments args() {
        return this.args;
    }

    public T source() {
        return this.source;
    }

    public <C> C source(Class<C> clazz) {
        return clazz.cast(this.source);
    }

}
