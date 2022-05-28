package me.ikevoodoo.smpcore.commands;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.commands.arguments.Argument;
import me.ikevoodoo.smpcore.commands.arguments.Arguments;
import me.ikevoodoo.smpcore.commands.arguments.OptionalFor;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public abstract class SMPCommand extends PluginProvider implements CommandExecutor {

    private final List<SMPCommand> subCommands;
    private SMPCommand parent;
    private final String name;
    private final HashMap<UUID, Long> cooldowns;

    private String permission;
    private CommandUsable usable = CommandUsable.ALL;
    private List<Argument> args = new ArrayList<>();

    public SMPCommand(SMPPlugin plugin, String name, String permission) {
        super(plugin);
        this.name = name;
        this.permission = permission;
        this.subCommands = new ArrayList<>();
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

        Arguments arguments = new Arguments(sender, args);
        if (!arguments.match(this.args)) {
            // get the sub command
            if(args.length > 0) {
                SMPCommand subCommand = getSubCommand(args[0]);
                if(subCommand != null) {
                    String msg = subCommand.getInvalidArgsMessage(sender, subCommand.name, arguments);
                    if(this.parent == null) {
                        sender.sendMessage(getInvalidArgsPrefix(sender, subCommand.name, arguments) + msg);
                        return true;
                    }
                }
            }
            sender.sendMessage(getInvalidArgsPrefix(sender, label, arguments) + getInvalidArgsMessage(sender, label, arguments));
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
            default -> {}
        }

        if (args.length == 0) {
            return this.execute(sender, arguments);
        }

        SMPCommand subCommand = getSubCommand(args[0]);
        if (subCommand != null) {
            return subCommand.onCommand(sender, command, label, List.of(args).subList(1, args.length).toArray(new String[0]));
        }

        boolean res = this.execute(sender, arguments);
        if (res) {
            String successMessage = getSuccessMessage(sender, label, arguments);
            if(successMessage != null)
                sender.sendMessage(successMessage);
        }
        return res;
    }

    private final SMPCommand getSubCommand(String name) {
        for (SMPCommand subCommand : this.subCommands) {
            if (subCommand.getName().equalsIgnoreCase(name)) {
                return subCommand;
            }
        }
        return null;
    }

    public abstract boolean execute(CommandSender sender, Arguments args);

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

    public String getInvalidArgsMessage(CommandSender sender, String label, Arguments args) {
        return label + " " + getPath(sender);
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

    public void setArgs(Argument... args) {
        this.args = List.of(args);
    }

    public final SMPCommand registerSubCommands(SMPCommand... commands) {
        this.subCommands.addAll(List.of(commands));
        this.subCommands.forEach(command -> command.parent = this);
        return this;
    }

    public final SMPCommand setUsable(CommandUsable usable) {
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
        for (Argument argument : args) {
            String[] border = getArgumentBorder(sender, argument);
            if (!argument.required()) {
                sb.append(border[0]).append(argument.name()).append(border[1]);
            } else {
                sb.append(" <").append(argument.name()).append(">");
            }
        }
        return sb.toString();
    }

    public final String getPermission() {
        return this.permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    private String[] getArgumentBorder(CommandSender sender, Argument argument) {
        OptionalFor optionalFor = argument.optionalFor();
        if(optionalFor == OptionalFor.ALL) {
            return new String[]{" [", "]"};
        }

        if(sender instanceof ConsoleCommandSender && optionalFor != OptionalFor.CONSOLE) {
            return new String[] {" <", ">"};
        }

        if(sender instanceof Player && optionalFor != OptionalFor.PLAYER) {
            return new String[] {" <", ">"};
        }

        if(sender instanceof BlockCommandSender && optionalFor != OptionalFor.COMMAND_BLOCK) {
            return new String[] {" <", ">"};
        }

        return new String[] {" [", "]"};
    }
    
    public final List<String> getPaths() {
        return getPath("", 0);
    }

    private List<String> getPath(String initial, int start) {
        List<String> paths = new ArrayList<>();
        StringBuilder sb = new StringBuilder(initial);
        for (int i = start; i < args.size() ; i++) {
            Argument argument = args.get(i);
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
