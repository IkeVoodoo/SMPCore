package me.ikevoodoo.smpcore.handlers;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.functions.SerializableConsumer;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.entity.Player;

import java.util.*;

public class JoinActionHandler extends PluginProvider {

    private final HashMap<UUID, List<SerializableConsumer<UUID>>> joinActions = new HashMap<>();
    private final List<SerializableConsumer<Player>> joinListeners = new ArrayList<>();

    public JoinActionHandler(SMPPlugin plugin) {
        super(plugin);
        /*this.joinActions.putAll(joinActions);
        this.joinListeners.addAll(joinListeners);*/
    }

    public void runOnJoin(UUID id, SerializableConsumer<UUID> runnable) {
        if (joinActions.containsKey(id)) joinActions.get(id).add(runnable);
        else joinActions.put(id, new ArrayList<>(List.of(runnable)));
    }

    public SerializableConsumer<Player> runAlwaysOnJoin(SerializableConsumer<Player> runnable) {
        joinListeners.add(runnable);
        return runnable;
    }

    public void cancelAlwaysOnJoin(SerializableConsumer<Player> runnable) {
        joinListeners.remove(runnable);
    }

    public void fire(UUID id) {
        if (joinActions.containsKey(id))
            joinActions.remove(id).forEach(uuidConsumer -> uuidConsumer.accept(id));
    }

    public void fire(Player player) {
        joinListeners.forEach(consumer -> consumer.accept(player));
    }

    public HashMap<UUID, List<SerializableConsumer<UUID>>> getJoinActions() {
        return joinActions;
    }

    public List<SerializableConsumer<Player>> getJoinListeners() {
        return joinListeners;
    }
}
