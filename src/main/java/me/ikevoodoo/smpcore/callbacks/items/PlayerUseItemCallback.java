package me.ikevoodoo.smpcore.callbacks.items;

import me.ikevoodoo.smpcore.callbacks.Callback;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

public interface PlayerUseItemCallback extends Callback {

    boolean useItem(Player player, ItemStack item, Action action);

}
