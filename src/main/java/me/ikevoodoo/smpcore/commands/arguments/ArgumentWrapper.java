package me.ikevoodoo.smpcore.commands.arguments;

import me.ikevoodoo.smpcore.commands.Context;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ArgumentWrapper {

    private static final String DEFAULT_OPTIONAL_FORMAT = "[%s]";
    private static final String DEFAULT_REQUIRED_FORMAT = "<%s>";

    private final Argument argument;

    private List<String> tabCompletion;

    public ArgumentWrapper(Argument argument) {
        this.argument = argument;
    }

    public List<String> getTabCompletion(Context<?> context) {
        if (!this.hasTabCompletion()) {
            return List.of(); // Should never happen
        }

        if (this.argument.cacheCompletion()) {
            if (this.tabCompletion == null) {
                this.tabCompletion = this.argument.tabCompletion().apply(context);
            }

            return this.tabCompletion;
        }

        return this.argument.tabCompletion().apply(context);
    }

    public String getFlatTabCompletion(Context<?> context) {
        var completion = this.getTabCompletion(context);

        var format = this.getFormat(context);
        if (completion.isEmpty()) {
            return format.formatted(this.argument.name());
        }

        return format.formatted(String.join("|", completion));
    }

    public boolean hasTabCompletion() {
        return this.argument.tabCompletion() != null;
    }

    public Argument getArgument() {
        return this.argument;
    }

    public String getFormat(CommandSender sender) {
        var optionalFor = this.argument.optionalFor();

        if(optionalFor == OptionalFor.ALL) {
            return DEFAULT_OPTIONAL_FORMAT;
        }

        if(sender instanceof ConsoleCommandSender && optionalFor != OptionalFor.CONSOLE) {
            return DEFAULT_REQUIRED_FORMAT;
        }

        if(sender instanceof Player && optionalFor != OptionalFor.PLAYER) {
            return DEFAULT_REQUIRED_FORMAT;
        }

        if(sender instanceof BlockCommandSender && optionalFor != OptionalFor.COMMAND_BLOCK) {
            return DEFAULT_REQUIRED_FORMAT;
        }

        return DEFAULT_OPTIONAL_FORMAT;
    }

    public String getFormat(Context<?> context) {
        return this.getFormat(context.source());
    }

    @Override
    public String toString() {
        return this.argument.toString();
    }
}
