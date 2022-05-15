package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.callbacks.blocks.PlayerPlaceBlockCallback;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerPlaceListener extends CallbackListener<PlayerPlaceBlockCallback> {
    public PlayerPlaceListener(SMPPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void on(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItemInHand();
        Block block = event.getBlock();
        Block placed = event.getBlockPlaced();

        PlayerPlaceBlockCallback callback = this.getCallback(item);
        if (callback == null) return;
        if(callback.onPlace(placed, block, item, player))
            event.setCancelled(true);
    }

}
