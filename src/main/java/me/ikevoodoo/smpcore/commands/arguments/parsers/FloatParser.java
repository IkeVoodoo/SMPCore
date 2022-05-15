package me.ikevoodoo.smpcore.commands.arguments.parsers;

import org.bukkit.command.CommandSender;

public class FloatParser implements BaseParser<Float> {
    @Override
    public Float parse(CommandSender executor, String input) {
        return Float.parseFloat(input);
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
