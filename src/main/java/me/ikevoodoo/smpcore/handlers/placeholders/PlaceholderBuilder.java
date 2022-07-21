package me.ikevoodoo.smpcore.handlers.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.function.Function;

public class PlaceholderBuilder {

    private final String author;
    private final String identifier;
    private final String version;

    private boolean persist;

    private boolean onlineRequiresPlayer;

    private final HashMap<String, Function<OfflinePlayer, String>> offlineHandlers = new HashMap<>();
    private final HashMap<String, Function<Player, String>> onlineHandlers = new HashMap<>();

    protected PlaceholderBuilder(String author, String identifier, String version) {
        this.author = author;
        this.identifier = identifier;
        this.version = version;
    }

    public PlaceholderBuilder persist() {
        this.persist = true;
        return this;
    }

    public PlaceholderBuilder onlineRequiresPlayer() {
        this.onlineRequiresPlayer = true;
        return this;
    }

    public PlaceholderBuilder offline(String placeholder, Function<OfflinePlayer, String> function) {
        this.offlineHandlers.put(placeholder, function);
        return this;
    }

    public PlaceholderBuilder online(String placeholder, Function<Player, String> function) {
        this.onlineHandlers.put(placeholder, function);
        return this;
    }

    public PlaceholderExpansion build() {
        return new PlaceholderExpansion() {
            @Override
            public @NotNull String getIdentifier() {
                return identifier;
            }

            @Override
            public @NotNull String getAuthor() {
                return author;
            }

            @Override
            public @NotNull String getVersion() {
                return version;
            }

            @Override
            public boolean persist() {
                return persist;
            }

            @Override
            public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
                Function<OfflinePlayer, String> fun = offlineHandlers.get(params);
                if (fun != null)
                    return fun.apply(player);

                return super.onRequest(player, params);
            }

            @Override
            public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
                if (player == null && onlineRequiresPlayer) return null;

                Function<Player, String> fun = onlineHandlers.get(params);
                if (fun != null)
                    return fun.apply(player);

                return null;
            }
        };
    }

    public PlaceholderExpansion register() {
        PlaceholderExpansion expansion = build();
        expansion.register();
        return expansion;
    }
}
