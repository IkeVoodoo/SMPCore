package me.ikevoodoo.smpcore.handlers;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import me.ikevoodoo.smpcore.texture.ResourcePackData;
import me.ikevoodoo.smpcore.utils.FileUtils;
import me.ikevoodoo.smpcore.utils.HashUtils;
import me.ikevoodoo.smpcore.utils.URLUtils;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("unused")
public class ResourcePackHandler extends PluginProvider {

    private final HashMap<String, ResourcePackData> resourcePacks = new HashMap<>();
    private final HashMap<String, ResourcePackData> resourcePackFiles = new HashMap<>();

    public ResourcePackHandler(SMPPlugin plugin) {
        super(plugin);
    }

    public void addResourcePack(String id, String location) {
        byte[] hash = URLUtils.ifValid(location, conn -> HashUtils.sha1Hash(conn.getInputStream()));
        if (hash == null) return;
        resourcePacks.put(id, new ResourcePackData(location, hash));
    }

    public void adResourcePackFile(String id, File file) throws IOException, NoSuchAlgorithmException {
        if (!file.exists()) return;

        getPlugin().getCacheFolder().mkdirs();

        File cacheFile = new File(getPlugin().getCacheFolder(), FileUtils.getName(file) + ".zip");
        if (cacheFile.exists()) {
            Files.deleteIfExists(cacheFile.toPath());
            cacheFile.createNewFile();
        }

        try(ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(cacheFile.toPath()))) {
            FileUtils.addToZip(file, zip);
        }

        resourcePackFiles.put(id, new ResourcePackData(cacheFile.getPath(), HashUtils.sha1Hash(Files.newInputStream(cacheFile.toPath()))));
    }

    /*public void addResourcePack(String id, File location) throws IOException, NoSuchAlgorithmException {
        if(location.exists()) {
            if(location.isDirectory()) {
                File zip = new File(location.getParentFile(), "pack.zip");
                if(zip.exists() && Files.deleteIfExists(zip.toPath()) && zip.createNewFile()) {
                    try(ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(zip.toPath()))) {
                        addToZip(location, zipOutputStream);
                    }
                    addResourcePack(id, zip);
                } else {
                    throw new IOException("Could not create zip file");
                }
                return;
            }
            String path = location.getPath()
                    .replace("\\", "/")
                    .replaceFirst(".*resourcepacks/", "/");
            if(path.isEmpty()) return;
            resourcePackFiles.put(id, new ResourcePackData(path, HashUtils.sha1Hash(Files.newInputStream(location.toPath()))));
            return;
        }

        throw new IOException("File does not exist");
    }*/

    public void applyResourcePack(Player player, String id) {
        ResourcePackData data = resourcePacks.get(id);
        if (data == null) {
            applyResourcePackFile(player, id);
            return;
        }
        player.setResourcePack(data.location(), data.hash());
    }

    public void applyResourcePackFile(Player player, String id) {
        ResourcePackData data = resourcePackFiles.get(id);
        if (data == null) return;
        player.setResourcePack(getPlugin().getServerIp() + ":8989/" + data.location(), data.hash());
    }

    public void removeResourcePack(String id) {
        resourcePacks.remove(id);
    }

    public void reload() throws IOException, NoSuchAlgorithmException {
        HashMap<String, ResourcePackData> newResourcePacks = new HashMap<>(resourcePacks);
        resourcePacks.clear();
        for (Map.Entry<String, ResourcePackData> entry : newResourcePacks.entrySet()) {
            addResourcePack(entry.getKey(), entry.getValue().location());
        }
    }
}
