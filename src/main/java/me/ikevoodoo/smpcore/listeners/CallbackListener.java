package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.callbacks.Callback;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class CallbackListener<T extends Callback> implements Listener {

    private final SMPPlugin plugin;

    private final HashMap<NamespacedKey, T> callbacks = new HashMap<>();

    CallbackListener(SMPPlugin plugin) {
        this.plugin = plugin;
    }

    protected T getCallback(String key) {
        return callbacks.get(new NamespacedKey(plugin, key));
    }

    protected HashMap<NamespacedKey, T> getCallbacks() {
        return callbacks;
    }

    protected T getCallback(ItemStack item) {
        if(item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return null;
        PersistentDataContainer container = meta.getPersistentDataContainer();
        for(Map.Entry<NamespacedKey, T> entry : callbacks.entrySet()) {
            if(container.has(entry.getKey(), PersistentDataType.INTEGER)) {
                return entry.getValue();
            }
        }
        return null;
    }

    public void addListener(String key, T callback) {
        addListener(new NamespacedKey(plugin, key), callback);
    }

    public void addListener(NamespacedKey key, T callback) {
        callbacks.put(key, callback);
    }

}
