package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatMessageListener implements Listener {

    private final SMPPlugin plugin;

    public ChatMessageListener(SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(plugin.getChatInputHandler().hasListener(player)) {
            event.setCancelled(true);
            plugin.getChatInputHandler().runListener(player, event.getMessage());
        }
    }

}
