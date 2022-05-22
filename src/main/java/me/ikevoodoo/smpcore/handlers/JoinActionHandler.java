package me.ikevoodoo.smpcore.handlers;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class JoinActionHandler extends PluginProvider {

    private final HashMap<UUID, List<Consumer<UUID>>> joinActions = new HashMap<>();
    private final List<Consumer<Player>> joinListeners = new ArrayList<>();

    public JoinActionHandler(SMPPlugin plugin) {
        super(plugin);
    }

    public void runOnJoin(UUID id, Consumer<UUID> runnable) {
        if (joinActions.containsKey(id)) joinActions.get(id).add(runnable);
        else joinActions.put(id, new ArrayList<>(List.of(runnable)));
    }

    public Consumer<Player> runAlwaysOnJoin(Consumer<Player> runnable) {
        joinListeners.add(runnable);
        return runnable;
    }

    public void cancelAlwaysOnJoin(Consumer<Player> runnable) {
        joinListeners.remove(runnable);
    }

    public void fire(UUID id) {
        if (joinActions.containsKey(id))
            joinActions.remove(id).forEach(uuidConsumer -> uuidConsumer.accept(id));
    }

    public void fire(Player player) {
        joinListeners.forEach(consumer -> consumer.accept(player));
    }
}
