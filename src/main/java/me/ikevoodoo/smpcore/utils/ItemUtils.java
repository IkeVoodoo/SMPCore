package me.ikevoodoo.smpcore.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.UUID;

public class ItemUtils {

    private ItemUtils() {

    }

    public static ItemStack setHeadOwner(ItemStack item, OfflinePlayer player) {
        ItemMeta meta = item.getItemMeta();
        if(!(meta instanceof SkullMeta skull))
            throw new IllegalArgumentException("Item is not a head!");
        skull.setOwningPlayer(player);
        item.setItemMeta(meta);
        return item;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack setHeadOwner(ItemStack item, String player) {
        return setHeadOwner(item, Bukkit.getOfflinePlayer(player));
    }

    public static ItemStack setHeadOwner(ItemStack item, UUID player) {
        return setHeadOwner(item, Bukkit.getOfflinePlayer(player));
    }

    public static ItemStack getHead(UUID id) {
        return setHeadOwner(new ItemStack(Material.PLAYER_HEAD), id);
    }

}
