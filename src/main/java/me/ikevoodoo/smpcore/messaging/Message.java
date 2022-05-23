package me.ikevoodoo.smpcore.messaging;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Message {

    private final BaseComponent msg;

    protected Message(BaseComponent[] components) {
        // merge components
        BaseComponent comp = components[0];
        for (int i = 1; i < components.length; i++)
            comp.addExtra(components[i]);

        this.msg = comp;
    }

    public Message send(Player player) {
        player.spigot().sendMessage(msg);
        return this;
    }

    public Message broadcast() {
        for(Player player : Bukkit.getOnlinePlayers())
            player.spigot().sendMessage(msg);
        return log();
    }

    public Message broadcast(String permission) {
        for (Player player : Bukkit.getOnlinePlayers())
            if (player.hasPermission(permission))
                player.spigot().sendMessage(msg);
        return log();
    }

    public Message broadcast(World world) {
        for (Player player : world.getPlayers())
            player.spigot().sendMessage(msg);
        return log();
    }
    
    public Message log() {
        Bukkit.getConsoleSender().sendMessage(msg.toLegacyText());
        return this;
    }

    public Message log(String prefix) {
        Bukkit.getConsoleSender().sendMessage(prefix + msg.toLegacyText());
        return this;
    }

}
