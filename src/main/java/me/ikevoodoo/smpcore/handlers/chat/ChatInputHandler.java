package me.ikevoodoo.smpcore.handlers.chat;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.callbacks.chat.ChatTransactionListener;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ChatInputHandler extends PluginProvider {

    private final HashMap<UUID, ChatTransactionListener> listeners = new HashMap<>();

    public ChatInputHandler(SMPPlugin plugin) {
        super(plugin);
    }

    public void onChatInput(Player player, ChatTransactionListener listener, String... messages) {
        for (String message : messages)
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        listeners.put(player.getUniqueId(), listener);
    }

    public void onChatInput(Player player, ChatTransactionListener listener, boolean cancellable, String... messages) {
        if(cancellable) onCancellableInput(player, listener, "§cType \"§ccancel§c\" to cancel.", "§aSuccessfully cancelled", messages);
        else onChatInput(player, listener, messages);
    }

    public void onCancellableInput(Player player, ChatTransactionListener listener, String cancelMessage, String cancelledMessage, String... messages) {
        for (String message : messages)
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', cancelMessage));
        listeners.put(player.getUniqueId(), s -> {
            if (s.equalsIgnoreCase("cancel")) {
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', cancelledMessage));
                listener.onComplete(false);
                return true;
            }

            return listener.onChat(s);
        });
    }

    public boolean hasListener(UUID id) {
        return listeners.containsKey(id);
    }

    public boolean hasListener(Player player) {
        return hasListener(player.getUniqueId());
    }

    public void removeListener(UUID id) {
        listeners.remove(id);
    }

    public void removeListener(Player player) {
        removeListener(player.getUniqueId());
    }

    public void runListener(UUID id, String message) {
        if (listeners.containsKey(id) && Boolean.TRUE.equals(listeners.get(id).onChat(message))) {
            listeners.remove(id).onComplete(true);
        }
    }

    public void runListener(Player player, String message) {
        runListener(player.getUniqueId(), message);
    }
}
