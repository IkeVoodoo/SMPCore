package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class ItemDamageListener implements Listener {

    private final SMPPlugin plugin;

    public ItemDamageListener(SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityCombust(EntityCombustEvent event) {
        var entity = event.getEntity();
        if (!(entity instanceof Item item)) return;

        var itemStack = item.getItemStack();
        var customItem = this.plugin.getItem(itemStack);
        if(customItem.isPresent() && !customItem.get().shouldAllowCombustion()) {
            event.setCancelled(true);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamageByBlock(EntityDamageByBlockEvent event) {
        var entity = event.getEntity();
        if (!(entity instanceof Item item)) return;

        var block = event.getDamager();
        if(block == null) return;

        var itemStack = item.getItemStack();
        var customItem = this.plugin.getItem(itemStack);
        if (customItem.isEmpty()) return;

        var shouldCancel = switch (block.getType()) {
            case CACTUS -> !customItem.get().shouldAllowCactusDamage();
            case FIRE, LAVA -> !customItem.get().shouldAllowCombustion();
            default -> false;
        };

        if(shouldCancel) {
            event.setCancelled(true);
        }
    }
}
