package me.ikevoodoo.smpcore.utils;

import me.ikevoodoo.smpcore.commands.SMPCommand;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;

import java.lang.reflect.Field;

public class CommandUtils {

    private static CommandMap map = null;

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
        }

        Command cmd = new Command(command.getName()) {
            @Override
            public boolean execute(CommandSender sender, String commandLabel, String[] args) {
                return command.onCommand(sender, null, commandLabel, args);
            }
        };

        cmd.setPermission(command.getPermission());

        map.register(command.getName(), cmd);
    }





}
