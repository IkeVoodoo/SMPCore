package me.ikevoodoo.smpcore.shared;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.config.ConfigData;
import org.bukkit.configuration.file.FileConfiguration;

public abstract class PluginProvider {

    private SMPPlugin plugin;

    public PluginProvider(SMPPlugin plugin) {
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
        return getPlugin().getConfigHandler().getConfig(name).getConfig();
    }

    public final void reloadConfig(String name) {
        getPlugin().getConfigHandler().getConfig(name).reload();
    }

    public final void saveConfig(String name) {
        getPlugin().getConfigHandler().getConfig(name).save();
    }

}
