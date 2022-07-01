package me.ikevoodoo.smpcore.menus;

import me.ikevoodoo.smpcore.events.MenuPageOpenEvent;
import me.ikevoodoo.smpcore.text.messaging.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;

public class MenuPage {

    private final HashMap<UUID, Inventory> inventories = new HashMap<>();
    private final HashMap<Integer, ItemStack> stacks = new HashMap<>();
    private final List<Consumer<Player>> openListeners = new ArrayList<>();
    private final Menu menu;
    private PageData data;

    protected MenuPage(PageData data, Menu menu) {
        this.data = data;
        this.menu = menu;
    }

    public Menu getParent() {
        return this.menu;
    }

    public int size() {
        return this.data.size();
    }
    public Message title() {
        return this.data.title();
    }

    public void data(PageData data) {
        this.data = data;
    }

    public Optional<ItemStack> item(int slot) {
        return Optional.ofNullable(this.stacks.get(slot));
    }

    public void item(ItemData... datas) {
        for (ItemData data : datas)
            this.stacks.put(data.slot(), data.stack());
        this.updateInventories(datas);
    }

    public void item(Player player, ItemData... datas) {
        Inventory inv = this.inventories.get(player.getUniqueId());
        if (inv == null) return;
        for (ItemData data : datas) {
            inv.setItem(data.slot(), data.stack());
        }
    }

    public void fill(ItemStack stack) {
        ItemStack[] items = new ItemStack[this.size()];
        Arrays.fill(items, stack);
        for (int i = 0; i < items.length; i++) {
            this.stacks.put(i, items[i]);
        }
        this.updateInventories();
    }

    public void open(Player player) {
        Inventory inventory = this.createInventory();
        this.inventories.put(player.getUniqueId(), inventory);
        MenuPageOpenEvent event = new MenuPageOpenEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
        this.openListeners.forEach(listener -> listener.accept(player));
        player.openInventory(inventory);
    }

    public void close(Player player) {
        this.inventories.remove(player.getUniqueId());
    }

    public void onOpen(Consumer<Player> listener) {
        this.openListeners.add(listener);
    }

    private Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(null, this.size(), this.title().text());
        stacks.forEach(inventory::setItem);
        return inventory;
    }

    private void updateInventories(ItemData... datas) {
        for (Inventory inventory : this.inventories.values()) {
            for (ItemData data : datas) {
                inventory.setItem(data.slot(), data.stack());
            }
        }
    }

    private void updateInventories() {
        for (Inventory inventory : this.inventories.values()) {
            for (Map.Entry<Integer, ItemStack> entry : this.stacks.entrySet()) {
                inventory.setItem(entry.getKey(), entry.getValue());
            }
        }
    }
}
