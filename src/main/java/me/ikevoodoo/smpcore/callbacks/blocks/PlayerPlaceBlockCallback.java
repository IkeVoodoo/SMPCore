package me.ikevoodoo.smpcore.callbacks.blocks;

import me.ikevoodoo.smpcore.callbacks.Callback;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface PlayerPlaceBlockCallback extends Callback {

    boolean onPlace(Block placedOn, Block placed, ItemStack stack, Player player);

}
