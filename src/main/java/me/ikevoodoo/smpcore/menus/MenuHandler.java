package me.ikevoodoo.smpcore.menus;

import java.awt.*;

public interface MenuHandler {

    default void onMenuOpen(Menu menu) {
    }

    default void onMenuClose(Menu menu) {
    }

    default void onMenuClick(Menu menu, MenuItem item) {
    }

}
