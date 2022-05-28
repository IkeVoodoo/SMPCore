package me.ikevoodoo.smpcore.menus;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MenuEvent {

    private final int slot;
    private final ItemStack item;
    private final Player player;

    protected MenuEvent(int slot, ItemStack item, Player player) {
        this.slot = slot;
        this.item = item;
        this.player = player;
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

}
