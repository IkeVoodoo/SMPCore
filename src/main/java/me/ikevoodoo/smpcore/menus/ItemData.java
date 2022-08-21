package me.ikevoodoo.smpcore.menus;

import org.bukkit.inventory.ItemStack;

public class ItemData {

    private final int slot;
    private final ItemStack stack;

    private ItemData(int slot, ItemStack stack) {
        this.slot = slot;
        this.stack = stack;
    }

    public int slot() {
        return this.slot;
    }

    protected ItemStack stack() {
        return this.stack;
    }

    public static ItemData of(int slot, ItemStack stack) {
        return new ItemData(slot, stack);
    }

    public ItemStack getStack() {
        return this.stack.clone();
    }

}
