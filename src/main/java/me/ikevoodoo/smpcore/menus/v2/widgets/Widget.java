package me.ikevoodoo.smpcore.menus.v2.widgets;

import me.ikevoodoo.smpcore.menus.v2.Page;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class Widget {

    private final Page page;

    public Widget(Page page) {
        this.page = page;
    }

    public boolean canDisplay() {
        return true;
    }

    public abstract void draw(Inventory inventory, Player player);

    protected void onClick(Player player) {

    }

    public Page getPage() {
        return page;
    }
}
