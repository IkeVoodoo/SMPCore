package me.ikevoodoo.smpcore.events;

import me.ikevoodoo.smpcore.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class MenuEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final Menu menu;
    private final int slot;
    private final ItemStack item;
    private final Player player;

    private boolean cancelled;

    public MenuEvent(Menu menu, int slot, ItemStack item, Player player) {
        this.menu = menu;
        this.slot = slot;
        this.item = item;
        this.player = player;
    }

    public Menu getMenu() {
        return this.menu;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void setCancelled(boolean b) {
        this.cancelled = b;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
