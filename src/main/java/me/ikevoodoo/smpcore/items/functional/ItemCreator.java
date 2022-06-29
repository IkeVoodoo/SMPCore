package me.ikevoodoo.smpcore.items.functional;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.menus.functional.FunctionalMenu;

public interface ItemCreator {

    default FunctionalItem createItem(SMPPlugin plugin) {
        return new FunctionalItem(plugin);
    }

}
