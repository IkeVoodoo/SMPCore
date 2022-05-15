package me.ikevoodoo.smpcore.commands.arguments.parsers;

import org.bukkit.command.CommandSender;

public class DoubleParser implements BaseParser<Double> {
    @Override
    public Double parse(CommandSender executor, String input) {
        return Double.parseDouble(input);
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
