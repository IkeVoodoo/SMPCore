package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.PlayerInventory;

public class InventoryEditListener implements Listener {

    private final SMPPlugin plugin;

    public InventoryEditListener(SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(InventoryEvent event) {
        if(event.getInventory() instanceof PlayerInventory inventory) {
            plugin.getInventoryActionHandler().callInventoryAction(inventory.getHolder() instanceof Player player ? player : null);
        }
    }

}
