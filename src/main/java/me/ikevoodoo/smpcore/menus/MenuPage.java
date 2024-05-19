package me.ikevoodoo.smpcore.menus;

import me.ikevoodoo.smpcore.events.MenuPageOpenEvent;
import me.ikevoodoo.smpcore.text.messaging.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MenuPage {

    private final HashMap<UUID, Inventory> inventories = new HashMap<>();
    private final HashMap<Integer, ItemStack> stacks = new HashMap<>();
    private final List<Consumer<Player>> openListeners = new ArrayList<>();
    private final Menu menu;
    private PageData data;

    private boolean allowItemActivation = true;

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

    public boolean allowItemActivation() {
        return this.allowItemActivation;
    }

    public void allowItemActivation(boolean allow) {
        this.allowItemActivation = allow;
    }

    public Optional<ItemStack> item(int slot) {
        return Optional.ofNullable(this.stacks.get(slot));
    }

    public Optional<ItemStack> item(Player player, int slot) {
        Inventory inv = this.inventories.get(player.getUniqueId());
        if (inv == null) return Optional.empty();

        return Optional.ofNullable(inv.getItem(slot));
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
            inv.setItem(getSlot(inv, data), data.stack());
        }
    }

    public List<ItemData> items() {
        return this.stacks
                .entrySet()
                .stream()
                .map(entry -> ItemData.of(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    public List<ItemData> items(Player player) {
        Inventory inv = this.inventories.get(player.getUniqueId());
        if (inv == null) return new ArrayList<>();
        List<ItemData> datas = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack == null) continue;
            datas.add(ItemData.of(i, stack));
        }
        return datas;
    }

    public void fill(ItemStack stack) {
        ItemStack[] items = new ItemStack[this.size()];
        Arrays.fill(items, stack);
        for (int i = 0; i < items.length; i++) {
            this.stacks.put(i, items[i]);
        }
        this.updateInventories();
    }

    public int last() {
        return this.size() - 1;
    }

    public int first() {
        return 0;
    }

    public int last(int row) {
        return Math.min(row, this.size() / 9) - 1;
    }

    public int first(int row) {
        return Math.max(Math.min(row, this.size() / 9) - 1, 0) * 9;
    }

    public void last(ItemStack stack) {
        this.stacks.put(last(), stack);
    }

    public void first(ItemStack stack) {
        this.stacks.put(first(), stack);
    }

    public void last(int row, ItemStack stack) {
        this.stacks.put(last(row), stack);
    }

    public void first(int row, ItemStack stack) {
        this.stacks.put(first(row), stack);
    }

    public void open(Player player) {
        Inventory inventory = this.createInventory();
        this.inventories.put(player.getUniqueId(), inventory);
        MenuPageOpenEvent event = new MenuPageOpenEvent(this, player);
        Bukkit.getPluginManager().callEvent(event);
        player.openInventory(inventory);
        Bukkit.getScheduler().scheduleSyncDelayedTask(getParent().getPlugin(), () -> {
            for (Consumer<Player> listener : this.openListeners)
                listener.accept(player);
        }, 1);
    }

    public void close(Player player) {
        this.inventories.remove(player.getUniqueId());
    }

    public void onOpen(Consumer<Player> listener) {
        this.openListeners.add(listener);
    }

    private Inventory createInventory() {
        Inventory inventory = Bukkit.createInventory(null, this.size(), this.title().legacyText());
        this.stacks.forEach(inventory::setItem);
        return inventory;
    }

    public void updateInventories(ItemData... datas) {
        for (Inventory inventory : this.inventories.values()) {
            for (ItemData data : datas) {
                inventory.setItem(getSlot(inventory, data), data.stack());
            }
        }
    }

    public void updateInventories() {
        for (Inventory inventory : this.inventories.values()) {
            for (Map.Entry<Integer, ItemStack> entry : this.stacks.entrySet()) {
                inventory.setItem(getSlot(inventory, entry.getKey()), entry.getValue());
            }
        }
    }

    private int getSlot(Inventory inv, ItemData data) {
        return getSlot(inv, data.slot());
    }

    private int getSlot(Inventory inv, int slot) {
        return slot < 0 ? inv.getSize() - slot : slot;
    }
}
