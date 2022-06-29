package me.ikevoodoo.smpcore.handlers;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.menus.Menu;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MenuHandler extends PluginProvider {

    private final HashMap<NamespacedKey, Menu> menus = new HashMap<>();

    public MenuHandler(SMPPlugin plugin) {
        super(plugin);
    }

    public void add(Menu menu) {
        this.menus.put(menu.id(), menu);
    }

    public Menu get(NamespacedKey id) {
        return this.menus.get(id);
    }

    public Menu get(String id) {
        return get(new NamespacedKey(this.getPlugin(), id));
    }

    public Menu get(Player player) {
        for (Menu menu : menus.values())
            if (menu.isViewer(player))
                return menu;
        return null;
    }

    public void remove(NamespacedKey id) {
        this.menus.remove(id);
    }

    public boolean has(NamespacedKey id) {
        return this.menus.containsKey(id);
    }

    public boolean has(String id) {
        return this.has(new NamespacedKey(this.getPlugin(), id));
    }

}
