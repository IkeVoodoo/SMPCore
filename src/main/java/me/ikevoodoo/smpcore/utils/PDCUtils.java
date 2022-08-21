package me.ikevoodoo.smpcore.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Optional;

public class PDCUtils {

    private PDCUtils() {

    }

    public static <T, Z> Optional<Pair<String, Z>> get(PersistentDataContainer container, PersistentDataType<T, Z> type) {
        for (NamespacedKey key : container.getKeys()) {
            try {
                Z z = container.get(key, type);
                return Optional.of(new Pair<>(key.getKey(), z));
            } catch (Exception ignored) {}
        }

        return Optional.empty();
    }

    public static boolean containsKey(PersistentDataContainer container, String key) {
        for (NamespacedKey namespacedKey : container.getKeys()) {
            if (namespacedKey.getKey().equals(key)) {
                return true;
            }
        }

        return false;
    }

    public static <T, Z> Optional<Pair<String, Z>> getPartial(PersistentDataContainer container, String key, PersistentDataType<T, Z> type) {
        for (NamespacedKey namespacedKey : container.getKeys()) {
            if (namespacedKey.getKey().startsWith(key)) {
                try {
                    Z z = container.get(namespacedKey, type);
                    return Optional.of(new Pair<>(namespacedKey.getKey(), z));
                } catch (Exception ignored) {
                    ignored.printStackTrace();
                }
            }
        }

        return Optional.empty();
    }

    public static void clear(PersistentDataContainer container) {
        for (NamespacedKey key : new HashSet<>(container.getKeys())) {
            container.remove(key);
        }
    }
}
