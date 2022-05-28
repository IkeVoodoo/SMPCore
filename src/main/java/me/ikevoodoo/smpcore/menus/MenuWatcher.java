package me.ikevoodoo.smpcore.menus;

import org.bukkit.entity.Player;

public interface MenuWatcher {

    default void onMenuOpen(Menu menu, Player player) {
    }

    default void onMenuClose(Menu menu, Player player) {
    }

    default boolean onMenuClick(Menu menu, MenuEvent item) {
        return false;
    }

}