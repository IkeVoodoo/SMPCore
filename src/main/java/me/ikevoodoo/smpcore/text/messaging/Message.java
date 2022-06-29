package me.ikevoodoo.smpcore.text.messaging;

import me.ikevoodoo.smpcore.text.messaging.utils.MessageUtils;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Message {

    private final TextComponent msg;
    private final List<BaseComponent> comps = new ArrayList<>();

    protected Message(BaseComponent[] components) {
        Collections.addAll(this.comps, components);
        this.msg = MessageUtils.toTextComponent(components);
    }

    public List<BaseComponent> components() {
        return new ArrayList<>(this.comps);
    }

    public TextComponent component() {
        return this.msg.duplicate();
    }

    public String text() {
        return this.msg.toLegacyText();
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
