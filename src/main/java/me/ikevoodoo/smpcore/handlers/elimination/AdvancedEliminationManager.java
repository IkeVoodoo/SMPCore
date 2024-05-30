package me.ikevoodoo.smpcore.handlers.elimination;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.caching.FastUUIDCache;
import me.ikevoodoo.smpcore.callbacks.eliminations.EliminationCallback;
import me.ikevoodoo.smpcore.callbacks.eliminations.EliminationType;
import me.ikevoodoo.smpcore.handlers.EliminationData;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.ApiStatus;

import javax.crypto.interfaces.PBEKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class AdvancedEliminationManager extends PluginProvider {

    private final NamespacedKey oldEliminatedKey;
    private final NamespacedKey eliminatedForKey;
    private final NamespacedKey eliminatedAtKey;
    private final NamespacedKey eliminatedMessageKey;

    private final AdvancedEliminationHandler handler;

    private final List<EliminationCallback> eliminationCallbacks = new ArrayList<>();
    private final List<EliminationCallback> reviveCallbacks = new ArrayList<>();
    private final List<BiConsumer<UUID, Number>> cacheListeners = new ArrayList<>();

    private final FastUUIDCache eliminationCache = new FastUUIDCache();

    public AdvancedEliminationManager(SMPPlugin plugin, AdvancedEliminationHandler handler) {
        super(plugin);
        this.handler = handler;
        this.oldEliminatedKey = makeKey("eliminated_player");
        this.eliminatedForKey = makeKey("eliminated_for");
        this.eliminatedAtKey = makeKey("eliminated_at");
        this.eliminatedMessageKey = makeKey("eliminated_message");
    }


    public Map<UUID, Number> getEliminatedPlayers() {
        return this.eliminationCache.fetch();
    }

    public boolean isEliminated(UUID playerId) {
        return this.eliminationCache.has(playerId);
    }

    public boolean isEliminated(Player player) {
        return this.isEliminated(player.getUniqueId());
    }

    public void eliminate(Player player, EliminationData data) {
        var id = player.getUniqueId();
        if (this.isEliminated(id)) {
            return;
        }

        var now = System.currentTimeMillis();
        var length = data.banTime();

        var cacheTime = now + length;

        this.setEliminated(id, cacheTime);

        var pdc = player.getPersistentDataContainer();
        pdc.set(this.eliminatedForKey, PersistentDataType.LONG, length);
        pdc.set(this.eliminatedAtKey, PersistentDataType.LONG, now);
        pdc.set(this.eliminatedMessageKey, PersistentDataType.STRING, data.message());

        this.updatePdc(pdc);

        this.handler.playerEliminated(player, data);
        this.fireCallbacks(this.eliminationCallbacks, EliminationType.ELIMINATED, player);
    }

    public void eliminate(Player player) {
        this.eliminate(player, EliminationData.infiniteTime());
    }

    public void eliminate(UUID id, EliminationData data) {
        var player = Bukkit.getPlayer(id);
        if (player == null) {
            return;
        }

        this.eliminate(player, data);
    }

    public void eliminate(UUID id) {
        this.eliminate(id, EliminationData.infiniteTime());
    }

    public void revive(Player player) {
        var id = player.getUniqueId();
        if (!this.isEliminated(id)) {
            return;
        }

        var data = this.getEliminationData(player);

        this.eliminationCache.remove(id);
        this.fireCacheListeners(id, null);

        var pdc = player.getPersistentDataContainer();
        this.updatePdc(pdc);
        pdc.remove(this.oldEliminatedKey);
        pdc.remove(this.eliminatedAtKey);

        this.handler.playerRevived(player, data);
        this.fireCallbacks(this.reviveCallbacks, EliminationType.REVIVED, player);
    }

    public void revive(UUID id) {
        Player player = Bukkit.getPlayer(id);
        if(player != null) {
            revive(player);
        }
    }

    public long getEliminationTime(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        Long value = container.get(this.eliminatedForKey, PersistentDataType.LONG);
        return value == null ? 0 : value;
    }

    public long getEliminatedAt(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        Long value = container.get(this.eliminatedAtKey, PersistentDataType.LONG);
        return value == null ? 0 : value;
    }

    public String getEliminatedMessage(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        String value = container.get(this.eliminatedMessageKey, PersistentDataType.STRING);
        return value == null ? EliminationData.DEFAULT_MESSAGE : value;
    }

    public EliminationData getEliminationData(Player player) {
        return new EliminationData(getEliminatedMessage(player), getEliminationTime(player));
    }

    @ApiStatus.Internal
    public void setEliminated(UUID id, long cacheTime) {
        this.eliminationCache.add(id, cacheTime);
        this.fireCacheListeners(id, cacheTime);
    }

    private void updatePdc(PersistentDataContainer container) {
        var old = container.getOrDefault(this.oldEliminatedKey, PersistentDataType.LONG, -1L);
        if (old == -1) return;

        container.remove(this.oldEliminatedKey);
        container.set(this.eliminatedForKey, PersistentDataType.LONG, old);
    }

    private void fireCacheListeners(UUID id, Number number) {
        for (var listener : this.cacheListeners) {
            listener.accept(id, number);
        }
    }

    private void fireCallbacks(List<EliminationCallback> callbacks, EliminationType type, Player player) {
        for (var callback : callbacks) {
            callback.whenTriggered(type, player);
        }
    }
}
