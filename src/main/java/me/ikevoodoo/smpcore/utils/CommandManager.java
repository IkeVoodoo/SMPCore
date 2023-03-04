package me.ikevoodoo.smpcore.utils;

import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.logging.Level;

public class CommandManager {

    private static CommandMap map = null;
    private static Constructor<PluginCommand> constructor;

    private CommandManager() {

    }

    public static void registerCommand(SMPCommand command) {
        if (!setupCommandMap()) return;

        try {
            PluginCommand cmd = constructor.newInstance(command.getName(), command.getPlugin());
            cmd.setExecutor(command);
            cmd.setTabCompleter(command);
            cmd.setPermission(command.getPermission());
            map.register(command.getPlugin().getName(), cmd);
        } catch (Exception e) {
            Bukkit.getLogger().log(Level.SEVERE,
                    () -> "Unable to register command %s as a PluginCommand, defaulting to Command."
                            .formatted(command.getName()));
            var cmd = createCommand(command);
            map.register(command.getPlugin().getName(), cmd);
        }
    }

    private static Command createCommand(SMPCommand command) {
        var cmd = new Command(command.getName()) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, String[] args) {
                return command.onCommand(sender, null, commandLabel, args);
            }
        };
        cmd.setPermission(command.getPermission());
        cmd.setLabel(command.getPlugin().getName());

        return cmd;
    }

    @SuppressWarnings("unchecked")
    private static boolean setupCommandMap() {
        if(map == null) {
            try {
                Field f = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);

                map = (CommandMap) f.get(Bukkit.getPluginManager());
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            try {
                constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                constructor.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        return true;
    }





}
