package me.ikevoodoo.smpcore.callbacks.items;

import me.ikevoodoo.smpcore.callbacks.Callback;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface PlayerUseItemCallback extends Callback {

    boolean useItem(Player player, ItemStack item, Action action);

    default List<Action> allowedActions() {
        return null;
    }

    /**
     Should blocks with an action stop the callback from being called?
     */
    default boolean blocksFirst() {
        return true;
    }

}
