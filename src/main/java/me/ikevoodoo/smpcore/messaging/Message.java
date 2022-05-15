package me.ikevoodoo.smpcore.messaging;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class Message {

    private BaseComponent message;

    protected Message(BaseComponent[] components) {
        // merge components
        BaseComponent comp = components[0];
        for (int i = 1; i < components.length; i++)
            comp.addExtra(components[i]);

        this.message = comp;
    }

    public Message send(Player player) {
        player.spigot().sendMessage(message);
        return this;
    }

    public Message broadcast() {
        for(Player player : Bukkit.getOnlinePlayers())
            player.spigot().sendMessage(message);
        Bukkit.getConsoleSender().sendMessage(message.toLegacyText());
        return this;
    }

    public Message broadcast(String permission) {
        for (Player player : Bukkit.getOnlinePlayers())
            if (player.hasPermission(permission))
                player.spigot().sendMessage(message);
        Bukkit.getConsoleSender().sendMessage(message.toLegacyText());
        return this;
    }

    public Message broadcast(World world) {
        for (Player player : world.getPlayers())
            player.spigot().sendMessage(message);
        Bukkit.getConsoleSender().sendMessage("[§cWB§r] " + world.getName() + ": " + message.toLegacyText());
        return this;
    }

}
