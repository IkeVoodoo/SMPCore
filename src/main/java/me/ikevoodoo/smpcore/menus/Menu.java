package me.ikevoodoo.smpcore.menus;

import me.ikevoodoo.smpcore.SMPPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.function.Consumer;

public class Menu extends MenuHolder {

    private final List<MenuPage> pages = new ArrayList<>();
    private final HashMap<UUID, Integer> viewingPage = new HashMap<>();
    private final HashMap<UUID, Integer> lastPages = new HashMap<>();

    private final NamespacedKey id;
    private final List<Consumer<Player>> openListeners = new ArrayList<>();

    public Menu(SMPPlugin plugin, String id) {
        super(plugin);
        this.id = makeKey(id);
    }

    public Menu(SMPPlugin plugin, NamespacedKey id) {
        super(plugin);
        this.id = id;
    }

    public NamespacedKey id() {
        return this.id;
    }

    public boolean is(NamespacedKey id) {
        return this.id.equals(id);
    }

    public boolean is(String id) {
        return this.is(this.getPlugin().makeKey(id));
    }

    public boolean isViewer(Player player) {
        return this.viewingPage.containsKey(player.getUniqueId());
    }

    public MenuPage page(PageData data) {
        MenuPage page = new MenuPage(data, this);
        this.pages.add(page);
        return page;
    }

    public Optional<MenuPage> page(int page) {
        if (this.pages.isEmpty() || page >= this.pages.size() || page < 0) return Optional.empty();
        return Optional.ofNullable(this.pages.get(page));
    }

    public Optional<MenuPage> page(Player player) {
        return this.page(this.viewingPage.get(player.getUniqueId()));
    }

    public void open(Player player) {
        if (this.pages.isEmpty()) return;
        closePage(player);
        MenuPage first = this.pages.get(0);
        first.open(player);
        this.openListeners.forEach(listener -> listener.accept(player));
        this.viewingPage.put(player.getUniqueId(), 0);
    }

    public void openLast(Player player) {
        if (this.pages.isEmpty()) return;
        closePage(player);
        int page = this.lastPages.getOrDefault(player.getUniqueId(), 0);
        MenuPage first = this.pages.get(page);
        first.open(player);
        this.openListeners.forEach(listener -> listener.accept(player));
        this.viewingPage.put(player.getUniqueId(), page);
    }

    public void next(Player player) {
        move(player, 1);
    }

    public void previous(Player player) {
        move(player, -1);
    }

    public void set(Player player, int page) {
        if (this.pages.isEmpty() || page >= this.pages.size() || page < 0) return;
        closePage(player);
        this.viewingPage.put(player.getUniqueId(), page);
        this.lastPages.put(player.getUniqueId(), page);
        MenuPage menu = this.pages.get(page);
        menu.open(player);
    }

    public void close(Player player) {
        closePage(player);
        player.closeInventory();
    }

    public void onOpen(Consumer<Player> listener) {
        this.openListeners.add(listener);
    }

    private void move(Player player, int amount) {
        if (this.pages.isEmpty()) return;
        Integer page = this.viewingPage.get(player.getUniqueId());
        if (page == null) return;
        page += amount;
        if (page >= this.pages.size() || page < 0) return;
        closePage(player);
        MenuPage menu = this.pages.get(page);
        menu.open(player);
        this.viewingPage.put(player.getUniqueId(), page);
        this.lastPages.put(player.getUniqueId(), page);
    }

    private void closePage(Player player) {
        Integer page = this.viewingPage.get(player.getUniqueId());
        if (page == null) return;
        MenuPage menu = this.pages.get(page);
        menu.close(player);
        this.viewingPage.remove(player.getUniqueId());
    }

}
