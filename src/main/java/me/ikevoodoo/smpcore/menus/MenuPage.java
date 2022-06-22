package me.ikevoodoo.smpcore.menus;

import me.ikevoodoo.smpcore.messaging.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class MenuPage {

    private final HashMap<UUID, Inventory> inventories = new HashMap<>();
    private final HashMap<Integer, ItemStack> stacks = new HashMap<>();
    private PageData data;

    protected MenuPage(PageData data) {
        this.data = data;
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

    public void open(Player player) {
        Inventory inventory = this.createInventory();
        this.inventories.put(player.getUniqueId(), inventory);
        player.openInventory(inventory);
    }

    public void close(Player player) {
        this.inventories.remove(player.getUniqueId());
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
}
