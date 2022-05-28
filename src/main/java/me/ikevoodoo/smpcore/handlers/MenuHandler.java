package me.ikevoodoo.smpcore.handlers;

import me.ikevoodoo.smpcore.menus.Menu;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class MenuHandler {

    private final HashMap<String, Menu> menus = new HashMap<>();

    public void registerMenu(Menu menu) {
        menus.put(menu.getId(), menu);
    }

    public Menu getMenu(String id) {
        return menus.get(id);
    }

    public Menu getMenu(Player player) {
        for (Menu menu : menus.values()) {
            if (menu.isViewer(player)) {
                return menu;
            }
        }
        return null;
    }

    public void showMenu(Player player, Menu menu) {
        menu.show(player);
    }

    public void showMenu(Player player, String id) {
        getMenu(id).show(player);
    }

    public void unregisterMenu(String id) {
        menus.remove(id);
    }

}
