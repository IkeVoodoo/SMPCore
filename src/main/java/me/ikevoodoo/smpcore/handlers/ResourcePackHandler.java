package me.ikevoodoo.smpcore.handlers;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import me.ikevoodoo.smpcore.texture.ResourcePackData;
import me.ikevoodoo.smpcore.utils.HashUtils;
import me.ikevoodoo.smpcore.utils.URLUtils;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class ResourcePackHandler extends PluginProvider {

    private final HashMap<String, ResourcePackData> resourcePacks = new HashMap<>();

    public ResourcePackHandler(SMPPlugin plugin) {
        super(plugin);
    }

    public void addResourcePack(String id, String url) {
        byte[] hash = URLUtils.ifValid(url, conn -> HashUtils.sha1Hash(conn.getInputStream()));
        if (hash == null) return;
        resourcePacks.put(id, new ResourcePackData(url, hash));
    }

    public void applyResourcePack(Player player, String id) {
        ResourcePackData data = resourcePacks.get(id);
        if (data == null) return;
        player.setResourcePack(data.url(), data.hash());
    }

    public void removeResourcePack(String id) {
        resourcePacks.remove(id);
    }
}
