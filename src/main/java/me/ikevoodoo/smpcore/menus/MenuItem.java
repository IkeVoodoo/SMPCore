package me.ikevoodoo.smpcore.menus;

import org.bukkit.inventory.ItemStack;

public class MenuItem {

    private int slot;
    private ItemStack item;

    protected MenuItem(int slot, ItemStack item) {
        this.slot = slot;
        this.item = item;
    }

    public int getSlot() {
        return slot;
    }

    public ItemStack getItem() {
        return item;
    }

}
