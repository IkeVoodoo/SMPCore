package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.callbacks.Callback;
import me.ikevoodoo.smpcore.callbacks.items.PlayerUseItemCallback;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class PlayerUseListener extends CallbackListener<PlayerUseItemCallback> {

    public PlayerUseListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerInteractEvent event) {
        boolean handled = handle(event.getItem(), event.getPlayer(), event.getAction());
        if (handled) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerInteractAtEntityEvent event) {
        boolean handled = handle(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer(), null);
        if (handled) event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerInteractEntityEvent event) {
        boolean handled = handle(event.getPlayer().getInventory().getItemInMainHand(), event.getPlayer(), null);
        if (handled) event.setCancelled(true);
    }

    private boolean handle(ItemStack item, Player player, Action action) {
        PlayerUseItemCallback callback = getCallback(item);
        if (callback == null) return false;
        return callback.useItem(player, item, action);
    }
}
