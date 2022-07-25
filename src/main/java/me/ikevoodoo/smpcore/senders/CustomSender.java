package me.ikevoodoo.smpcore.senders;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class CustomSender {

    private CustomSender() {

    }

    public static CustomSender as() {
        return new CustomSender();
    }

    private CommandSender sender;
    private boolean log;

    public CustomSender sender(CommandSender sender) {
        this.sender = sender;
        return this;
    }

    public CustomSender console() {
        return this.sender(Bukkit.getConsoleSender());
    }

    public CustomSender player(Player player) {
        return this.sender(player);
    }

    public CustomSender player(String player) {
        return this.player(Bukkit.getPlayer(player));
    }

    public CustomSender player(UUID uuid) {
        return this.player(Bukkit.getPlayer(uuid));
    }

    public CustomSender log() {
        this.log = true;
        return this;
    }

    public CustomSender noLog() {
        this.log = false;
        return this;
    }

    public boolean isLog() {
        return log;
    }

    public CommandSender getSender() {
        return sender;
    }

}
