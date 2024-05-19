package me.ikevoodoo.smpcore.text.messaging;

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
    private final List<BaseComponent> textComponents = new ArrayList<>();
    private final List<BaseComponent> textComponentsView = Collections.unmodifiableList(this.textComponents);

    protected Message(BaseComponent[] components) {
        Collections.addAll(this.textComponents, components);
        this.msg = new TextComponent(components);
    }

    public List<BaseComponent> components() {
        return this.textComponentsView;
    }

    public TextComponent component() {
        return this.msg.duplicate();
    }

    public String legacyText() {
        return this.msg.toLegacyText();
    }

    public Message send(Player player) {
        player.spigot().sendMessage(this.msg);
        return this;
    }

    public Message broadcast() {
        Bukkit.getOnlinePlayers().forEach(this::send);
        return sendToConsole();
    }

    public Message broadcast(String permission) {
        for (var player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission(permission)) continue;

            this.send(player);
        }

        return sendToConsole();
    }

    public Message broadcast(World world) {
        world.getPlayers().forEach(this::send);
        return sendToConsole();
    }

    public Message broadcast(World world, String permission) {
        for (var player : world.getPlayers()) {
            if (!player.hasPermission(permission)) continue;

            this.send(player);
        }

        return sendToConsole();
    }
    
    public Message sendToConsole() {
        Bukkit.getConsoleSender().sendMessage(this.msg.toLegacyText());
        return this;
    }

    public Message sendToConsole(String prefix) {
        Bukkit.getConsoleSender().sendMessage(prefix + this.msg.toLegacyText());
        return this;
    }

}
