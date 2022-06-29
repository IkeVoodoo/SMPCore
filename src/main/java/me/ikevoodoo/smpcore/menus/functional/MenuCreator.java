package me.ikevoodoo.smpcore.menus.functional;

import me.ikevoodoo.smpcore.SMPPlugin;

public interface MenuCreator {

    default FunctionalMenu createMenu(SMPPlugin plugin) {
        return new FunctionalMenu(plugin);
    }

}
