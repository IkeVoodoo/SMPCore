package me.ikevoodoo.smpcore.utils;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

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

}
