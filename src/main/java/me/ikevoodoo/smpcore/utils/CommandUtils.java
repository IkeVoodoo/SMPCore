package me.ikevoodoo.smpcore.utils;

import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class CommandUtils {

    private static CommandMap map = null;
    private static Constructor<PluginCommand> constructor;

    private CommandUtils() {

    }

    public static void register(SMPCommand command) {

        if(map == null) {
            try {
                Field f = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                f.setAccessible(true);
                map = (CommandMap) f.get(Bukkit.getServer());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            try {
                constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
                constructor.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            PluginCommand cmd = constructor.newInstance(command.getName(), command.getPlugin());
            cmd.setExecutor(command);
            cmd.setTabCompleter(command);
            cmd.setPermission(command.getPermission());
            map.register(command.getName(), cmd);
        } catch (Exception e) {
            System.err.println("Unable to register PluginCommand, defaulting to Command");
            Command cmd = new Command(command.getName()) {
                @Override
                public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                    return command.onCommand(sender, null, commandLabel, args);
                }
            };
            cmd.setPermission(command.getPermission());
            cmd.setLabel(command.getPlugin().getName());
            map.register(command.getName(), cmd);
        }
    }





}
