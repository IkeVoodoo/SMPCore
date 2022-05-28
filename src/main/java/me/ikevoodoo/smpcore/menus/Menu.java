package me.ikevoodoo.smpcore.menus;

import me.ikevoodoo.smpcore.SMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class Menu {

    private final List<Menu> children;
    private Menu parent;

    private final String id;

    private final List<MenuWatcher> watchers;

    private ItemStack[] items;
    private int size;
    private String title;
    private boolean movingBlocked;

    private final List<Player> viewers = new ArrayList<>();

    public Menu(SMPPlugin plugin, String id) {
        this.id = id;
        this.parent = null;
        this.children = new ArrayList<>();
        this.watchers = new ArrayList<>();

        this.title = "Menu: " + id;
        this.size = 54;
        this.items = new ItemStack[this.size];
        plugin.getMenuHandler().registerMenu(this);
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
        Inventory inv = Bukkit.createInventory(null, this.size, this.title);
        for (int i = 0; i < this.size; i++) {
            if(items[i] != null) inv.setItem(i, items[i].clone());
            else inv.setItem(i, null);
        }
        this.viewers.add(player);
        player.openInventory(inv);
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

    public void set(int slot, ItemStack item) {
        this.items[slot] = item;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSize(int size) {
        this.size = size;
        ItemStack[] newItems = new ItemStack[size];
        System.arraycopy(this.items, 0, newItems, 0, Math.min(this.items.length, size));
        this.items = newItems;
    }

    public void addWatcher(MenuWatcher watcher) {
        this.watchers.add(watcher);
    }

    public void listen(int slot, Predicate<MenuEvent> runnable) {
        this.watchers.add(new MenuWatcher() {
            @Override
            public boolean onMenuClick(Menu menu, MenuEvent item) {
                if(item.getSlot() == slot)
                    return runnable.test(item);
                return false;
            }
        });
    }

    public boolean isViewer(Player player) {
        return this.viewers.contains(player);
    }

    public void onOpen(Player player) {
        for (MenuWatcher watcher : this.watchers) {
            watcher.onMenuOpen(this, player);
        }
    }

    public void onClose(Player player) {
        this.viewers.remove(player);
        for (MenuWatcher watcher : this.watchers) {
            watcher.onMenuClose(this, player);
        }
    }

    public void blockItems() {
        this.movingBlocked = true;
    }

    public void unblockItems() {
        this.movingBlocked = false;
    }

    public boolean itemUpdated(int slot, ItemStack item, Player player) {
        MenuEvent menuItem = new MenuEvent(slot, item, player);
        var ref = new Object() {
            boolean cancelled = false;
        };
        this.watchers.forEach(watcher -> ref.cancelled = watcher.onMenuClick(this, menuItem) || this.movingBlocked || ref.cancelled);
        return ref.cancelled;
    }

}
