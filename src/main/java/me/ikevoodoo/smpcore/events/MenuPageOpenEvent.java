package me.ikevoodoo.smpcore.events;

import me.ikevoodoo.smpcore.menus.MenuPage;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class MenuPageOpenEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    private final MenuPage menu;
    private final Player player;

    private boolean cancelled;

    public MenuPageOpenEvent(MenuPage menu, Player player) {
        this.menu = menu;
        this.player = player;
    }

    public MenuPage getPage() {
        return this.menu;
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
