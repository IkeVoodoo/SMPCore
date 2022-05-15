package me.ikevoodoo.smpcore.commands.arguments.parsers;

import org.bukkit.command.CommandSender;

public class LongParser implements BaseParser<Long> {
    @Override
    public Long parse(CommandSender executor, String input) {
        return Long.parseLong(input);
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
