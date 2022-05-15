package me.ikevoodoo.smpcore.commands.arguments.parsers;

import org.bukkit.command.CommandSender;

import java.util.UUID;

public class UUIDParser implements BaseParser<UUID> {
    @Override
    public UUID parse(CommandSender executor, String input) {
        return UUID.fromString(input);
    }

    @Override
    public boolean canParse(String input) {
        try {
            UUID.fromString(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
