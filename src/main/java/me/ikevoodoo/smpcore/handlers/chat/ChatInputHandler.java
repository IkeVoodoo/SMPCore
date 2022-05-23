package me.ikevoodoo.smpcore.handlers.chat;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import me.ikevoodoo.smpcore.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Function;

public class ChatInputHandler extends PluginProvider {

    private final HashMap<UUID, Function<String, Boolean>> listeners = new HashMap<>();

    public ChatInputHandler(SMPPlugin plugin) {
        super(plugin);
    }

    public void onChatInput(UUID id, Function<String, Boolean> consumer) {
        listeners.put(id, consumer);
    }

    public void onChatInput(Player player, Function<String, Boolean> consumer, String... messages) {
        onChatInput(player.getUniqueId(), consumer);
        for (String message : messages)
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
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
        if (listeners.containsKey(id) && Boolean.TRUE.equals(listeners.get(id).apply(message)))
            listeners.remove(id);
    }

    public void runListener(Player player, String message) {
        runListener(player.getUniqueId(), message);
    }
}
