package me.ikevoodoo.smpcore.handlers;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.callbacks.eliminations.EliminationCallback;
import me.ikevoodoo.smpcore.callbacks.eliminations.EliminationType;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class EliminationHandler extends PluginProvider {

    private final NamespacedKey eliminationKey;
    private final NamespacedKey bannedAtKey;

    private final List<EliminationCallback> eliminationCallbacks = new ArrayList<>();
    private final List<EliminationCallback> reviveCallbacks = new ArrayList<>();

    public EliminationHandler(SMPPlugin plugin) {
        super(plugin);
        eliminationKey = makeKey("eliminated_player");
        bannedAtKey = makeKey("eliminated_at");
    }

    public void eliminate(Player player) {
        eliminate(player, Long.MAX_VALUE);
    }

    public void eliminate(UUID id) {
        Player player = Bukkit.getPlayer(id);
        if(player != null) {
            eliminate(player);
        }
    }

    public void eliminate(Player player, long banTime) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(eliminationKey, PersistentDataType.LONG, banTime);
        container.set(bannedAtKey, PersistentDataType.LONG, System.currentTimeMillis());
        eliminationCallbacks.forEach(callback -> callback.whenTriggered(EliminationType.ELIMINATED, player));
    }

    public void eliminate(UUID id, long banTime) {
        Player player = Bukkit.getPlayer(id);
        if(player != null) {
            eliminate(player, banTime);
        }
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

    public long getBannedAt(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        Long value = container.get(bannedAtKey, PersistentDataType.LONG);
        return value == null ? 0 : value;
    }

    public void revive(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.remove(eliminationKey);
        container.remove(bannedAtKey);
        reviveCallbacks.forEach(callback -> callback.whenTriggered(EliminationType.REVIVED, player));
    }

    public void revive(UUID id) {
        Player player = Bukkit.getPlayer(id);
        if(player != null) {
            revive(player);
        }
    }

    public void reviveAll() {
        Bukkit.getOnlinePlayers().forEach(this::revive);
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            reviveOffline(offlinePlayer);
        }
    }

    public void eliminateAll() {
        eliminateAll(Long.MAX_VALUE);
    }

    public void eliminateAll(long banTime) {
        Bukkit.getOnlinePlayers().forEach(player -> eliminate(player, banTime));
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            eliminateOffline(offlinePlayer, banTime);
        }
    }

    public void listen(EliminationType type, EliminationCallback callback) {
        if(type == EliminationType.ELIMINATED) {
            eliminationCallbacks.add(callback);
            return;
        }

        reviveCallbacks.add(callback);
    }

    public void reviveOffline(OfflinePlayer player) {
        if (player == null)
            return;
        if (player.isOnline())
            this.revive(player.getPlayer());
        else getPlugin().getJoinActionHandler().runOnJoin(player.getUniqueId(), this::revive);
    }

    public void eliminateOffline(OfflinePlayer player) {
        eliminateOffline(player, Long.MAX_VALUE);
    }

    public void eliminateOffline(OfflinePlayer player, long banTime) {
        if (player == null)
            return;
        if (player.isOnline())
            this.eliminate(player.getUniqueId(), banTime);
        else getPlugin().getJoinActionHandler().runOnJoin(player.getUniqueId(), id -> eliminate(id, banTime));
    }
    
}
