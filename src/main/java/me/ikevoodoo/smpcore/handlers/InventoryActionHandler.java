package me.ikevoodoo.smpcore.handlers;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class InventoryActionHandler extends PluginProvider {

    private final List<Consumer<Player>> inventoryListener = new ArrayList<>();

    public InventoryActionHandler(SMPPlugin plugin) {
        super(plugin);
    }

    public Consumer<Player> onInventoryAction(Consumer<Player> listener) {
        inventoryListener.add(listener);
        return listener;
    }

    public void removeInventoryAction(Consumer<Player> listener) {
        inventoryListener.remove(listener);
    }

    public void callInventoryAction(Player player) {
        if(player == null) return;
        inventoryListener.forEach(listener -> listener.accept(player));
    }

}
