package me.ikevoodoo.smpcore.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.CommandNode;
import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public abstract class CoreCommand extends PluginProvider implements TabExecutor {

    private final CommandNode<CommandSender> root;
    private final String permission;

    public CoreCommand(SMPPlugin plugin, String name, String permission) {
        super(plugin);
        this.permission = permission;

        this.root = plugin.getCommandDispatcher().register(LiteralArgumentBuilder.literal(name));
    }

    @Override
    public final boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        var parsed = getPlugin().getCommandDispatcher().parse(
                label + " " + String.join(" ", args),
                sender
        );

        try {
            return getPlugin().getCommandDispatcher().execute(parsed) == 1;
        } catch (CommandSyntaxException e) {
            sender.sendMessage("§c" + e.getMessage());
            return true;
        }
    }

    @Nullable
    @Override
    public final List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        var parse = getPlugin().getCommandDispatcher().parse(alias + " " + String.join(" ", args), sender);
        try {
            var completion = getPlugin().getCommandDispatcher().getCompletionSuggestions(parse).get();

            return completion.getList().stream().map(Suggestion::getText).toList();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }


}
