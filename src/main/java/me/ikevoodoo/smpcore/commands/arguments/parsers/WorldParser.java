package me.ikevoodoo.smpcore.commands.arguments.parsers;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class WorldParser implements BaseParser<World>  {
    @Override
    public World parse(CommandSender executor, String input) {
        try {
            return Bukkit.getWorld(UUID.fromString(input));
        } catch (Exception e) {
            return Bukkit.getWorld(input);
        }
    }

    @Override
    public boolean canParse(String input) {
        return parse(null, input) != null;
    }
}
