package me.ikevoodoo.smpcore.commands.arguments.parsers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class OfflinePlayerParser implements BaseParser<OfflinePlayer> {
    @Override
    @SuppressWarnings("deprecation")
    public OfflinePlayer parse(CommandSender executor, String input) {
        try {
            return Bukkit.getOfflinePlayer(UUID.fromString(input));
        } catch (Exception e) {
            return Bukkit.getOfflinePlayer(input);
        }
    }

    @Override
    public boolean canParse(String input) {
        return true;
    }
}