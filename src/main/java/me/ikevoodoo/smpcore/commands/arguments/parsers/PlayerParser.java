package me.ikevoodoo.smpcore.commands.arguments.parsers;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PlayerParser implements BaseParser<Player> {
    @Override
    public Player parse(CommandSender executor, String input) {
        try {
            return Bukkit.getPlayer(UUID.fromString(input));
        } catch (Exception e) {
            return Bukkit.getPlayer(input);
        }
    }

    @Override
    public boolean canParse(String input) {
        return parse(null, input) != null;
    }
}
