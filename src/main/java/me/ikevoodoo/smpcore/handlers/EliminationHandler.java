package me.ikevoodoo.smpcore.handlers;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.callbacks.eliminations.EliminationCallback;
import me.ikevoodoo.smpcore.callbacks.eliminations.EliminationType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public final class EliminationHandler {

    private final SMPPlugin plugin;
    private final NamespacedKey eliminationKey;

    private final List<EliminationCallback> eliminationCallbacks = new ArrayList<>();
    private final List<EliminationCallback> reviveCallbacks = new ArrayList<>();

    public EliminationHandler(SMPPlugin plugin) {
        this.plugin = plugin;
        eliminationKey = new NamespacedKey(plugin, "eliminated_player");
    }

    public void eliminate(Player player) {
        eliminate(player, Long.MAX_VALUE);
    }

    public void eliminate(Player player, long banTime) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(eliminationKey, PersistentDataType.LONG, banTime);
        eliminationCallbacks.forEach(callback -> callback.whenTriggered(EliminationType.ELIMINATED, player));
    }

    public boolean isEliminated(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        return container.has(eliminationKey, PersistentDataType.LONG);
    }

    public long getBanTime(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        Long value = container.get(eliminationKey, PersistentDataType.LONG);
        return value == null ? 0 : value;
    }

    public void revive(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.remove(eliminationKey);
        reviveCallbacks.forEach(callback -> callback.whenTriggered(EliminationType.REVIVED, player));
    }

    public void reviveAll() {
        Bukkit.getOnlinePlayers().forEach(this::revive);
    }

    public void eliminateAll() {
        Bukkit.getOnlinePlayers().forEach(this::eliminate);
    }

    public void listen(EliminationType type, EliminationCallback callback) {
        if(type == EliminationType.ELIMINATED) {
            eliminationCallbacks.add(callback);
            return;
        }

        reviveCallbacks.add(callback);
    }

    public void reviveOffline(OfflinePlayer player) {

    }

    
}
