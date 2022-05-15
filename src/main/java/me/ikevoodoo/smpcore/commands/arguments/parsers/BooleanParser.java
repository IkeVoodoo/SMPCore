package me.ikevoodoo.smpcore.commands.arguments.parsers;

import org.bukkit.command.CommandSender;

public class BooleanParser implements BaseParser<Boolean> {
    @Override
    public Boolean parse(CommandSender executor, String input) {
        return Boolean.parseBoolean(input);
    }

    @Override
    public boolean canParse(String input) {
        try {
            parse(null, input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
