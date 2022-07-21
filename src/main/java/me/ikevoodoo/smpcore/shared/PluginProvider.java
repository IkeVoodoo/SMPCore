package me.ikevoodoo.smpcore.shared;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.config.ConfigData;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public abstract class PluginProvider {

    private final SMPPlugin plugin;

    protected PluginProvider(SMPPlugin plugin) {
        this.plugin = plugin;
    }

    public final SMPPlugin getPlugin() {
        return plugin;
    }

    public final <T> T getPlugin(Class<T> clazz) {
        return clazz.cast(plugin);
    }

    public final FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public final FileConfiguration getConfig(String name) {
        return getConfigData(name).getConfig();
    }

    public final ConfigData getConfigData(String name) {
        return getPlugin().getConfigHandler().getConfig(name);
    }

    public final void reloadConfig(String name) {
        getConfigData(name).reload();
    }

    public final void saveConfig(String name) {
        getConfigData(name).save();
    }

    public final NamespacedKey makeKey(String key) {
        return new NamespacedKey(plugin, key);
    }

    public final YamlConfiguration getYamlConfig(String name) {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), name));
    }

}
