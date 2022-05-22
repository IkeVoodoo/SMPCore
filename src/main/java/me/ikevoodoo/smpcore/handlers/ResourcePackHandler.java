package me.ikevoodoo.smpcore.handlers;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import me.ikevoodoo.smpcore.texture.ResourcePackData;
import me.ikevoodoo.smpcore.utils.HashUtils;
import me.ikevoodoo.smpcore.utils.URLUtils;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@SuppressWarnings("unused")
public class ResourcePackHandler extends PluginProvider {

    private final HashMap<String, ResourcePackData> resourcePacks = new HashMap<>();
    private final HashMap<String, ResourcePackData> resourcePackFiles = new HashMap<>();

    public ResourcePackHandler(SMPPlugin plugin) {
        super(plugin);
    }

    public void addResourcePack(String id, String location) throws IOException, NoSuchAlgorithmException {
        File file = new File(location);
        if(file.exists()) {
            addResourcePack(id, file);
            return;
        }
        byte[] hash = URLUtils.ifValid(location, conn -> HashUtils.sha1Hash(conn.getInputStream()));
        if (hash == null) return;
        resourcePacks.put(id, new ResourcePackData(location, hash));
    }

    public void addResourcePack(String id, File location) throws IOException, NoSuchAlgorithmException {
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
    }

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
        player.setResourcePack(data.location(), data.hash());
    }

    public void removeResourcePack(String id) {
        resourcePacks.remove(id);
    }

    private void addToZip(File folder, ZipOutputStream os) {
        File[] files = folder.listFiles();
        if (files == null) return;
        for (File file : files) {
            if (file.isDirectory()) {
                addToZip(file, os);
                continue;
            }
            ZipEntry entry = new ZipEntry(folder.toPath().relativize(file.toPath()).toString());
            try {
                os.putNextEntry(entry);
                Files.copy(file.toPath(), os);
                os.closeEntry();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
