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

    public CustomSender console() {
        this.sender = Bukkit.getConsoleSender();
        return this;
    }

    public CustomSender player(Player player) {
        this.sender = player;
        return this;
    }

    public CustomSender player(String player) {
        this.sender = Bukkit.getPlayer(player);
        return this;
    }

    public CustomSender player(UUID uuid) {
        this.sender = Bukkit.getPlayer(uuid);
        return this;
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
