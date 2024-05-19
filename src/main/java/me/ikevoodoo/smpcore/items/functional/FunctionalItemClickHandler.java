package me.ikevoodoo.smpcore.items.functional;

import me.ikevoodoo.smpcore.items.ItemClickResult;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface FunctionalItemClickHandler {

    ItemClickResult onClick(Player player, ItemStack stack, ItemClickResult current);

}
