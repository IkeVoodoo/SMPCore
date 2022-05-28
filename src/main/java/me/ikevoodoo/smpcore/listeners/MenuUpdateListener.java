package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

public class MenuUpdateListener extends SMPListener {

    public MenuUpdateListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player player)) return;
        Menu menu = getPlugin().getMenuHandler().getMenu(player);
        if (menu != null) {
            ItemStack item = event.getCurrentItem();
            if(item == null || item.getType().isAir())
                item = event.getCursor();
            event.setCancelled(menu.itemUpdated(event.getSlot(), item, player));
        }
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player player)) return;
        Menu menu = getPlugin().getMenuHandler().getMenu(player);
        if (menu != null) {
            menu.onClose(player);
        }
    }

    @EventHandler
    public void on(InventoryOpenEvent event) {
        if(!(event.getPlayer() instanceof Player player)) return;
        Menu menu = getPlugin().getMenuHandler().getMenu(player);
        if (menu != null) {
            menu.onOpen(player);
        }
    }

}
