package me.ikevoodoo.smpcore.commands.arguments.parsers;

import org.bukkit.command.CommandSender;

public class IntParser implements BaseParser<Integer> {
    @Override
    public Integer parse(CommandSender executor, String input) {
        return Integer.parseInt(input);
    }

    @Override
    public boolean canParse(String input) {
        try {
            Integer.parseInt(input);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
