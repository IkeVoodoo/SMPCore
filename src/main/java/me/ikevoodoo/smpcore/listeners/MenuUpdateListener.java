package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.events.MenuEvent;
import me.ikevoodoo.smpcore.items.ItemClickResult;
import me.ikevoodoo.smpcore.menus.Menu;
import me.ikevoodoo.smpcore.menus.MenuPage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class MenuUpdateListener extends SMPListener {

    public MenuUpdateListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player player)) return;

        Menu menu = getPlugin().getMenuHandler().get(player);
        if (menu == null) return;

        Optional<MenuPage> optionalPage = menu.page(player);
        if (optionalPage.isEmpty()) {
            return;
        }

        MenuPage page = optionalPage.get();

        if (shouldDisallowAction(event.getAction())) {
            event.setResult(Event.Result.DENY);
            return;
        }

        if (event.getView().getBottomInventory() == event.getClickedInventory()) return;

        ItemStack item = event.getCurrentItem();
        if(item == null || item.getType().isAir())
            return;

        if (!page.allowNormalItemPickup()) {
            event.setResult(Event.Result.DENY);
        }

        var customItem = getPlugin().getItem(item);
        if (customItem.isEmpty()) {
            MenuEvent e = new MenuEvent(menu, event.getSlot(), item, player);
            Bukkit.getPluginManager().callEvent(e);
            if (e.isCancelled()) {
                event.setResult(Event.Result.DENY);
            }
            return;
        }

        if (!page.allowItemActivation()) return;

        ItemClickResult result = customItem.get().tryClick(player, event.getCurrentItem(), null);
        if (result.shouldCancel()) {
            event.setResult(Event.Result.DENY);
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

    private boolean shouldDisallowAction(InventoryAction action) {
        return switch (action) {
            case COLLECT_TO_CURSOR, MOVE_TO_OTHER_INVENTORY, HOTBAR_SWAP, HOTBAR_MOVE_AND_READD -> true;
            default -> false;
        };
    }
}
