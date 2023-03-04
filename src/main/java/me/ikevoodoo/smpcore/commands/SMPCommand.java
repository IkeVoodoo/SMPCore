package me.ikevoodoo.smpcore.commands;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.ArgumentWrapper;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Stream;

public abstract class SMPCommand extends PluginProvider implements CommandExecutor, TabCompleter {

    private final HashMap<String, SMPCommand> subCommands;
    private SMPCommand parent;
    private final String name;
    private final HashMap<UUID, Long> cooldowns;

    private String permission;
    private CommandUsable usable = CommandUsable.ALL;
    private List<ArgumentWrapper> args = new ArrayList<>();

    public SMPCommand(SMPPlugin plugin, String name, String permission) {
        super(plugin);
        this.name = name;
        this.permission = permission;
        this.subCommands = new HashMap<>();
        this.cooldowns = new HashMap<>();
    }

    public SMPCommand(SMPPlugin plugin, String name) {
        this(plugin, name, null);
    }

    @Override
    public final boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(permission != null && !permission.isBlank() && !sender.hasPermission(permission)) {
            sender.sendMessage(getNoPermissionMessage(sender));
            return true;
        }


        Arguments arguments = new Arguments(sender, this.args, args);
        var context = new Context<>(sender, arguments);
        if (!arguments.match()) {
            if(args.length > 0) {
                SMPCommand subCommand = this.subCommands.get(args[0]);
                if(subCommand != null) {
                    String msg = subCommand.getInvalidArgsMessage(context, subCommand.name);
                    if(this.parent != null) {
                        sender.sendMessage(getInvalidArgsPrefix(sender, subCommand.name, arguments) + msg);
                        return true;
                    }
                }
            }
            sender.sendMessage(getInvalidArgsPrefix(sender, label, arguments) + getInvalidArgsMessage(context, label));
            return true;
        }

        switch (this.usable) {
            case PLAYER -> {
                if (!(sender instanceof Player plr)) {
                    sender.sendMessage(getInvalidSenderMessage(usable));
                    return true;
                } else if(hasCooldown(plr)) {
                    sender.sendMessage(getCooldownMessage(plr));
                    return true;
                }
            }
            case CONSOLE -> {
                if (!(sender instanceof ConsoleCommandSender)) {
                    sender.sendMessage(getInvalidSenderMessage(usable));
                    return true;
                }
            }
            case COMMAND_BLOCK -> {
                if (!(sender instanceof BlockCommandSender)) {
                    sender.sendMessage(getInvalidSenderMessage(usable));
                    return true;
                }
            }
        }

        if (args.length == 0) {
            return this.execute(new Context<>(sender, arguments));
        }

        SMPCommand subCommand = this.subCommands.get(args[0]);
        if (subCommand != null) {
            return subCommand.onCommand(sender, command, label, List.of(args).subList(1, args.length).toArray(new String[0]));
        }

        boolean res = this.execute(context);
        if (res) {
            String successMessage = getSuccessMessage(sender, label, arguments);
            if(successMessage != null)
                sender.sendMessage(successMessage);
        }
        return res;
    }

    @Override
    public final List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> out = new ArrayList<>();
        if(strings.length > 0) {
            SMPCommand cmd = this.subCommands.get(strings[0]);
            if (cmd != null) {
                String[] subArgs = new String[strings.length - 1];
                System.arraycopy(strings, 1, subArgs, 0, subArgs.length);
                out.addAll(cmd.onTabComplete(commandSender, command, s, subArgs));
                return out;
            }
        }
        this.subCommands.forEach((key, sub) -> out.add(key));

        List<ArgumentWrapper> sub;
        if (strings.length - 1 > this.args.size()) sub = new ArrayList<>();
        else sub = this.args.subList(Math.max(0, strings.length - 1), this.args.size());

        Arguments arguments = new Arguments(commandSender, sub, strings);
        Context<?> ctx = new Context<>(commandSender, arguments);

        for (ArgumentWrapper wrapper : sub) {
            out.addAll(getCompatible(ctx, wrapper));
            if (wrapper.getArgument().required()) break;
        }

        return out;
    }

    private List<String> getCompatible(Context<?> ctx, ArgumentWrapper wrapper) {
        List<String> compatible = new ArrayList<>();
        Argument arg = wrapper.getArgument();

        if (wrapper.hasTabCompletion()) {
            List<String> completion = wrapper.getTabCompletion(ctx);
            if (completion != null && !completion.isEmpty()) return completion;
        }

        if (arg.type() == Player.class) {
            Bukkit.getOnlinePlayers().forEach(plr -> compatible.add(plr.getName()));
            return compatible;
        }

        if (arg.type() == OfflinePlayer.class) {
            Bukkit.getOnlinePlayers().forEach(plr -> compatible.add(plr.getName()));
            Arrays.stream(Bukkit.getOfflinePlayers()).forEach(plr -> compatible.add(plr.getName()));
            return compatible;
        }

        var format = wrapper.getFormat(ctx);
        compatible.add(format.formatted(arg.name()));
        return compatible;
    }

    public abstract boolean execute(Context<?> context);

    public String getInvalidSenderMessage(CommandUsable usable) {
        return switch (usable) {
            case PLAYER -> "§cYou must be a player to use this command.";
            case CONSOLE -> "§cYou must be the console to use this command.";
            case COMMAND_BLOCK -> "§cYou must be a command block to use this command.";
            case ALL -> null;
        };
    }

    public String getInvalidArgsPrefix(CommandSender sender, String label, Arguments args) {
        return "§cUsage: §f/";
    }

    public String getInvalidArgsMessage(Context<?> context, String label) {
        var differences = context.args().findDifferences(this.args);
        var builder = new StringBuilder(label).append(" ");
        var contextArgs = context.args();
        var raw = contextArgs.getRaw();

        for (int i = 0; i < raw.size(); i++) {
            var difference = differences.get(i);
            if (difference != null) {
                builder.append(difference.getFlatTabCompletion(context)).append(" ");
                continue;
            }

            builder.append(raw.get(i)).append(" ");
        }

        for (int i = raw.size(); i < contextArgs.typeLength(); i++) {
            var difference = differences.get(i);
            if (difference != null) {
                builder.append(difference.getFlatTabCompletion(context)).append(" ");
                continue;
            }

            builder.append(contextArgs.getArgument(i).getFlatTabCompletion(context)).append(" ");
        }

        return builder.toString().trim();
    }

    public String getSuccessMessage(CommandSender sender, String label, Arguments args) {
        return null;
    }

    public String getNoPermissionMessage(CommandSender sender) {
        return "§cYou do not have permission to use this command.";
    }

    public String getCooldownMessage(Player plr) {
        return "§cYou must wait &4" + (getCooldown(plr) / 1000) + " &cseconds before using this command again.";
    }

    public final void setArgs(Argument... args) {
        this.args = Stream.of(args).map(ArgumentWrapper::new).toList();
    }

    public final SMPCommand registerSubCommands(SMPCommand... commands) {
        for (SMPCommand sub : commands) {
            this.subCommands.put(sub.getName(), sub);
            sub.parent = this;
        }
        return this;
    }

    public final SMPCommand setUsable(CommandUsable usable) {
        if (usable != null)
            this.usable = usable;
        return this;
    }

    public final SMPCommand setCooldown(UUID id, long cooldown) {
        this.cooldowns.put(id, cooldown);
        return this;
    }

    public final SMPCommand addCooldown(UUID id, long cooldown) {
        this.cooldowns.put(id, this.cooldowns.getOrDefault(id, 0L) + cooldown);
        return this;
    }

    public final SMPCommand subtractCooldown(UUID id, long cooldown) {
        this.cooldowns.put(id, this.cooldowns.getOrDefault(id, 0L) - cooldown);
        return this;
    }

    public final long getCooldown(UUID id) {
        return this.cooldowns.getOrDefault(id, 0L);
    }

    public final boolean hasCooldown(UUID id) {
        return this.cooldowns.getOrDefault(id, 0L) > 0L;
    }

    public final SMPCommand setCooldown(Player player, long cooldown) {
        return this.setCooldown(player.getUniqueId(), cooldown);
    }

    public final SMPCommand addCooldown(Player player, long cooldown) {
        return this.addCooldown(player.getUniqueId(), cooldown);
    }

    public final SMPCommand subtractCooldown(Player player, long cooldown) {
        return this.subtractCooldown(player.getUniqueId(), cooldown);
    }

    public final boolean hasCooldown(Player player) {
        return this.hasCooldown(player.getUniqueId());
    }

    public final long getCooldown(Player player) {
        return this.getCooldown(player.getUniqueId());
    }

    public final SMPCommand clearCooldowns() {
        this.cooldowns.clear();
        return this;
    }

    public final String getName() {
        return this.name;
    }

    public final String getPath(CommandSender sender) {
        StringBuilder sb = new StringBuilder();
        for (ArgumentWrapper wrapper : args) {
            Argument argument = wrapper.getArgument();

            if (!argument.required()) {
                sb.append(wrapper.getFormat(sender).formatted(argument.name()));
            } else {
                sb.append(" <").append(argument.name()).append(">");
            }
        }
        return sb.toString();
    }

    public final String getPermission() {
        return this.permission;
    }

    public final void setPermission(String permission) {
        this.permission = permission;
    }
    
    public final List<String> getPaths() {
        return getPath("", 0);
    }

    private List<String> getPath(String initial, int start) {
        List<String> paths = new ArrayList<>();
        StringBuilder sb = new StringBuilder(initial);
        for (int i = start; i < args.size() ; i++) {
            ArgumentWrapper wrapper = args.get(i);
            Argument argument = wrapper.getArgument();
            if(!argument.required()) {
                String s = sb + " <" + argument.name() + ">";
                sb.append(" [").append(argument.name()).append("]");
                paths.addAll(getPath(s, i + 1));
            } else {
                sb.append(" <").append(argument.name()).append(">");
            }
        }
        paths.add(sb.toString());
        return paths;
    }
}
