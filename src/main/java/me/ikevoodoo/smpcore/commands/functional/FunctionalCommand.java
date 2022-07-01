package me.ikevoodoo.smpcore.commands.functional;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.CommandUsable;
import me.ikevoodoo.smpcore.commands.Context;
import me.ikevoodoo.smpcore.commands.SMPCommand;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.functional.loop.FunctionalLoopBase;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import me.ikevoodoo.smpcore.utils.Pair;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class FunctionalCommand extends PluginProvider implements FunctionalLoopBase {

    private String name;

    private String perm;

    private CommandUsable usable;

    private final List<Runnable> bound = new ArrayList<>();
    private final List<Consumer<Context<?>>> consumers = new ArrayList<>();
    private final List<Supplier<Pair<CommandReturn, Object>>> then = new ArrayList<>();
    private final List<Function<Context<?>, Pair<CommandReturn, Object>>> consumerThen = new ArrayList<>();
    private final List<Argument> arguments = new ArrayList<>();
    private final List<FunctionalCommand> subFunctional = new ArrayList<>();
    private final List<SMPCommand> subCommands = new ArrayList<>();


    protected FunctionalCommand(SMPPlugin plugin) {
        super(plugin);
    }

    public FunctionalCommand name(String name) {
        this.name = name;
        return this;
    }

    public FunctionalCommand perm(String perm) {
        this.perm = perm;
        return this;
    }

    public FunctionalCommand usable(CommandUsable usable) {
        this.usable = usable;
        return this;
    }


    public FunctionalCommand bind(Runnable bound) {
        if (bound != null)
            this.bound.add(bound);
        return this;
    }

    public FunctionalCommand bind(Consumer<Context<?>> bound) {
        if (bound != null)
            this.consumers.add(bound);
        return this;
    }

    public FunctionalCommand then(Supplier<Pair<CommandReturn, Object>> then) {
        if (then != null)
            this.then.add(then);
        return this;
    }

    public FunctionalCommand then(Function<Context<?>, Pair<CommandReturn, Object>> then) {
        if (then != null)
            this.consumerThen.add(then);
        return this;
    }

    public FunctionalCommand arg(Argument argument) {
        if (argument != null)
            this.arguments.add(argument);
        return this;
    }

    public FunctionalCommand arg(Argument argument, Argument... arguments) {
        this.arg(argument);
        for (Argument arg : arguments)
            this.arg(arg);
        return this;
    }

    public FunctionalCommand sub(SMPCommand command) {
        this.subCommands.add(command);
        return this;
    }

    public FunctionalCommand sub(FunctionalCommand command) {
        this.subFunctional.add(command);
        return this;
    }

    public void register() {
        this.getPlugin().addCommand(getCommand());
    }

    private SMPCommand getCommand() {
        if (this.name == null)
            throw new IllegalStateException("Cannot register a command without a name! Plugin: " + this.getPlugin().getName());
        SMPCommand command = new SMPCommand(this.getPlugin(), this.name, null) {
            @Override
            public boolean execute(Context<?> context) {
                for (Runnable bind : bound)
                    bind.run();

                for (Consumer<Context<?>> consumer : consumers)
                    consumer.accept(context);

                for (Supplier<Pair<CommandReturn, Object>> supplier : then) {
                    Pair<CommandReturn, Object> functionReturn = supplier.get();
                    switch (functionReturn.getFirst()) {
                        case SEND_MESSAGE -> context.source().sendMessage(String.valueOf(functionReturn.getSecond()));
                        case EXECUTE_AS_SENDER ->  Bukkit.dispatchCommand(context.source(), String.valueOf(functionReturn.getSecond()));
                        case EXECUTE -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.valueOf(functionReturn.getSecond()));
                    }
                }

                for (Function<Context<?>, Pair<CommandReturn, Object>> supplier : consumerThen) {
                    Pair<CommandReturn, Object> functionReturn = supplier.apply(context);
                    switch (functionReturn.getFirst()) {
                        case SEND_MESSAGE -> context.source().sendMessage(String.valueOf(functionReturn.getSecond()));
                        case EXECUTE_AS_SENDER ->  Bukkit.dispatchCommand(context.source(), String.valueOf(functionReturn.getSecond()));
                        case EXECUTE -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.valueOf(functionReturn.getSecond()));
                    }
                }
                return true;
            }
        };
        command.setUsable(this.usable);
        command.setArgs(this.arguments.toArray(new Argument[0]));
        command.setPermission(this.perm);
        List<SMPCommand> sub = new ArrayList<>(this.subCommands);
        for (FunctionalCommand cmd : this.subFunctional)
            sub.add(cmd.getCommand());
        command.registerSubCommands(sub.toArray(new SMPCommand[0]));
        return command;
    }
}
