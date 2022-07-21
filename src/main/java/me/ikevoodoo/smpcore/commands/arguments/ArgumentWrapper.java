package me.ikevoodoo.smpcore.commands.arguments;

import me.ikevoodoo.smpcore.commands.Context;

import java.util.List;

public class ArgumentWrapper {

    private final Argument argument;

    private List<String> tabCompletion;

    public ArgumentWrapper(Argument argument) {
        this.argument = argument;
    }

    public List<String> getTabCompletion(Context<?> context) {
        if (this.argument.cacheCompletion()) {
            if (this.hasTabCompletion() && this.tabCompletion == null)
                return this.tabCompletion = this.argument.tabCompletion().apply(context);

            return this.tabCompletion;
        }

        return this.argument.tabCompletion().apply(context);
    }

    public boolean hasTabCompletion() {
        return this.argument.tabCompletion() != null;
    }

    public Argument getArgument() {
        return this.argument;
    }
}
