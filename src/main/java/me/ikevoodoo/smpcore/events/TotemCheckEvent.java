package me.ikevoodoo.smpcore.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.PlayerInventory;

public class TotemCheckEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private boolean hasTotem;

    public TotemCheckEvent(Player player) {
        this.player = player;
        PlayerInventory inv = player.getInventory();
        this.hasTotem = inv.getItemInMainHand().getType() == Material.TOTEM_OF_UNDYING
                     || inv.getItemInOffHand().getType() == Material.TOTEM_OF_UNDYING;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public boolean hasTotem() {
        return this.hasTotem;
    }

    public void setHasTotem(boolean hasTotem) {
        this.hasTotem = hasTotem;
    }

    public Player getPlayer() {
        return this.player;
    }

    public PlayerInventory getInventory() {
    	return this.player.getInventory();
    }

}
