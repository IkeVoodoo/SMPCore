package me.ikevoodoo.smpcore.handlers;

import me.ikevoodoo.smpcore.menus.Menu;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MenuHandler {

    private final HashMap<NamespacedKey, Menu> menus = new HashMap<>();

    public void add(Menu menu) {
        this.menus.put(menu.id(), menu);
    }

    public Menu get(NamespacedKey id) {
        return this.menus.get(id);
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

}
