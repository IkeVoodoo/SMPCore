package me.ikevoodoo.smpcore.menus;

import org.bukkit.Bukkit;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public class Menu {

    private final List<Menu> children;
    private Menu parent;
    private String id;

    private final List<MenuHandler> handlers;

    private Inventory inv;

    private String title;

    public Menu(String id) {
        this.id = id;
        this.parent = null;
        this.children = new ArrayList<>();
        this.handlers = new ArrayList<>();

        this.inv = Bukkit.createInventory(null, 54, this.title = "Menu: " + id);
    }

    public void setParent(Menu parent) {
        this.parent.removeChild(this);
        this.parent = parent;
    }

    public void addChild(Menu child) {
        child.setParent(this);
        this.children.add(child);
    }

    public void removeChild(Menu child) {
        this.children.remove(child);
    }

    public String getId() {
        return this.id;
    }

    public void show(Player player) {

    }

    public void close(Player player) {
        player.closeInventory();
    }

    public void showChild(String id, Player player) {
        for (Menu child : this.children) {
            if (child.getId().equals(id)) {
                child.show(player);
            }
        }
    }

    public void setTitle(String title) {
        List<HumanEntity> viewers = new ArrayList<>(this.inv.getViewers());
        this.inv = Bukkit.createInventory(null, this.inv.getSize(), this.title = title);
        viewers.forEach(humanEntity -> humanEntity.openInventory(this.inv));
    }

    public void setSize(int size) {
        List<HumanEntity> viewers = new ArrayList<>(this.inv.getViewers());
        this.inv = Bukkit.createInventory(null, size, this.title);
        viewers.forEach(humanEntity -> humanEntity.openInventory(this.inv));
    }

    public void addHandler(MenuHandler handler) {
        this.handlers.add(handler);
    }

}
