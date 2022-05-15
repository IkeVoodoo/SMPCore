package me.ikevoodoo.smpcore.commands.arguments.parsers;

import org.bukkit.command.CommandSender;

public class StringParser implements BaseParser<String> {
    @Override
    public String parse(CommandSender executor, String input) {
        return input;
    }

    @Override
    public boolean canParse(String input) {
        return true;
    }
}
