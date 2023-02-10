package me.ikevoodoo.smpcore.handlers;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.caching.FastUUIDCache;
import me.ikevoodoo.smpcore.callbacks.eliminations.EliminationCallback;
import me.ikevoodoo.smpcore.callbacks.eliminations.EliminationType;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiConsumer;

public final class EliminationHandler extends PluginProvider {

    private final NamespacedKey eliminationKey;
    private final NamespacedKey bannedAtKey;
    private final NamespacedKey bannedMessageKey;

    private final List<EliminationCallback> eliminationCallbacks = new ArrayList<>();
    private final List<EliminationCallback> reviveCallbacks = new ArrayList<>();
    private final List<BiConsumer<UUID, Number>> cacheListeners = new ArrayList<>();

    private final FastUUIDCache cache = new FastUUIDCache();

    public EliminationHandler(SMPPlugin plugin) {
        super(plugin);
        eliminationKey = makeKey("eliminated_player");
        bannedAtKey = makeKey("eliminated_at");
        bannedMessageKey = makeKey("eliminated_message");
    }

    public void save(File file) throws IOException {
        try (OutputStream stream = Files.newOutputStream(file.toPath())) {
            cache.save(stream);
        }
    }

    public void load(File file) throws IOException {
        try (InputStream stream = Files.newInputStream(file.toPath())) {
            cache.load(stream);
        }
    }

    public Map<UUID, Number> getEliminatedPlayers() {
        return cache.fetch();
    }

    public void eliminate(Player player) {
        eliminate(player, EliminationData.infiniteTime());
    }

    public void eliminate(UUID id) {
        Player player = Bukkit.getPlayer(id);
        if(player != null) {
            eliminate(player);
        }
    }

    public void eliminate(UUID id, EliminationData eliminationData) {
        Player player = Bukkit.getPlayer(id);
        if(player != null) {
            eliminate(player, eliminationData);
        }
    }

    public void markEliminated(UUID id, EliminationData eliminationData) {
        addToCache(id, eliminationData.getCacheTime());
    }

    public void eliminate(Player player, EliminationData eliminationData) {
        if (isEliminated(player)) {
            return;
        }
        markEliminated(player.getUniqueId(), eliminationData);
        PersistentDataContainer container = player.getPersistentDataContainer();
        container.set(eliminationKey, PersistentDataType.LONG, eliminationData.banTime());
        container.set(bannedAtKey, PersistentDataType.LONG, System.currentTimeMillis());
        container.set(bannedMessageKey, PersistentDataType.STRING, eliminationData.message());
        eliminationCallbacks.forEach(callback -> callback.whenTriggered(EliminationType.ELIMINATED, player));
    }

    public boolean isEliminated(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        return container.has(eliminationKey, PersistentDataType.LONG);
    }

    public boolean isEliminated(UUID id) {
        Player player = Bukkit.getPlayer(id);
        if(player != null) {
            return isEliminated(player);
        }
        return this.cache.has(id);
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

    public String getBanMessage(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer();
        String value = container.get(bannedMessageKey, PersistentDataType.STRING);
        return value == null ? EliminationData.DEFAULT_MESSAGE : value;
    }

    public EliminationData getEliminationData(Player player) {
        return new EliminationData(getBanMessage(player), getBanTime(player));
    }

    public void revive(Player player) {
        if (!isEliminated(player)) {
            return;
        }
        removeFromCache(player.getUniqueId());
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
        eliminateAll(EliminationData.infiniteTime());
    }

    public void eliminateAll(EliminationData eliminationData) {
        List<UUID> excluded = new ArrayList<>();
        Bukkit.getOnlinePlayers().forEach(player -> {
            if(!excluded.contains(player.getUniqueId())) {
                excluded.add(player.getUniqueId());
                eliminate(player, eliminationData);
            }
        });
        for (OfflinePlayer offlinePlayer : Bukkit.getOfflinePlayers()) {
            if (!excluded.contains(offlinePlayer.getUniqueId())) {
                eliminateOffline(offlinePlayer, eliminationData);
            }
        }
    }

    public void listen(EliminationType type, EliminationCallback callback) {
        if(type == EliminationType.ELIMINATED) {
            eliminationCallbacks.add(callback);
            return;
        }

        reviveCallbacks.add(callback);
    }

    public void onCacheUpdated(BiConsumer<UUID, Number> listener) {
        cacheListeners.add(listener);
    }

    public void reviveOffline(OfflinePlayer player) {
        if (player == null)
            return;
        if (player.isOnline())
            this.revive(player.getPlayer());
        else {
            getPlugin().getJoinActionHandler().runOnJoin(player.getUniqueId(), this::revive);
            removeFromCache(player.getUniqueId());
        }
    }

    public void eliminateOffline(OfflinePlayer player) {
        eliminateOffline(player, EliminationData.infiniteTime());
    }

    public void eliminateOffline(OfflinePlayer player, EliminationData eliminationData) {
        if (player == null)
            return;
        if (player.isOnline())
            this.eliminate(player.getUniqueId(), eliminationData);
        else {
            getPlugin().getJoinActionHandler().runOnJoin(player.getUniqueId(), id -> eliminate(id, eliminationData));
            this.markEliminated(player.getUniqueId(), eliminationData);
        }
    }

    private void removeFromCache(UUID id) {
        cache.remove(id);
        this.cacheListeners.forEach(listener -> listener.accept(id, null));
    }

    private void addToCache(UUID id, Number value) {
        cache.add(id, value);
        this.cacheListeners.forEach(listener -> listener.accept(id, value));
    }
    
}
