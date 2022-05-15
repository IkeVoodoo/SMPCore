package me.ikevoodoo.smpcore.commands.arguments.parsers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;

public interface BaseParser<T> {

    T parse(CommandSender executor, String input);

    boolean canParse(String input);
}
