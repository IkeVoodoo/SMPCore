package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.events.MenuEvent;
import me.ikevoodoo.smpcore.items.ItemClickResult;
import me.ikevoodoo.smpcore.menus.Menu;
import me.ikevoodoo.smpcore.menus.MenuPage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

public class MenuUpdateListener extends SMPListener {

    public MenuUpdateListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player player)) return;

        if (event.getView().getBottomInventory() == event.getClickedInventory()) return;

        Menu menu = getPlugin().getMenuHandler().get(player);
        if (menu != null) {
            MenuPage page = menu.page(player).orElseThrow();

            ItemStack item = event.getCurrentItem();
            if(item == null || item.getType().isAir())
                return;

            getPlugin().getItem(item).ifPresentOrElse(custom -> {
                if (!page.allowItemActivation()) return;

                ItemClickResult result = custom.tryClick(player, event.getCurrentItem(), null);
                if (result.shouldCancel())
                    event.setCancelled(true);
            }, () -> {
                MenuEvent e = new MenuEvent(menu, event.getSlot(), item, player);
                Bukkit.getPluginManager().callEvent(e);
                if (e.isCancelled())
                    event.setCancelled(true);
            });

        }
    }

    @EventHandler
    public void on(InventoryCloseEvent event) {
        if(!(event.getPlayer() instanceof Player player)) return;
        Menu menu = getPlugin().getMenuHandler().get(player);
        if (menu != null) {
            menu.close(player);
        }
    }

}
