package me.ikevoodoo.smpcore.shared;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.config2.Configuration;
import me.ikevoodoo.smpcore.utils.Pair;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@SuppressWarnings("unused")
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


    public final NamespacedKey makeKey(String key) {
        return new NamespacedKey(plugin, key);
    }

    public List<String> listConfigs() {
        return this.plugin.getConfigHandler().listConfigs();
    }

    public List<String> listYmlConfigs() {
        return this.plugin.getConfigHandler().listYmlConfigs();
    }

    public void registerConfig(Pair<Configuration, ?> config) {
        this.plugin.getConfigHandler().registerConfig(config);
    }

    public <T> T getConfig(String name) {
        return this.plugin.getConfigHandler().getConfig(name);
    }

    public <T> T getConfig(Class<T> clazz) {
        return this.plugin.getConfigHandler().getConfig(clazz);
    }

    public <T> Map<String, Object> extractValues(String name, Function<T, Map<String, Object>> extracter) {
        return this.plugin.getConfigHandler().extractValues(name, extracter);
    }

    public <T> Map<String, Object> extractValues(Class<T> clazz, Function<T, Map<String, Object>> extracter) {
        return this.plugin.getConfigHandler().extractValues(clazz, extracter);
    }

    public boolean exists(String name) {
        return this.plugin.getConfigHandler().exists(name);
    }

    public FileConfiguration getYmlConfig(String name) {
        return this.plugin.getConfigHandler().getYmlConfig(name);
    }

    public File getFile(String name) {
        return this.plugin.getConfigHandler().getFile(name);
    }

    public void saveConfig(String name) throws IOException {
        this.plugin.getConfigHandler().saveConfig(name);
    }

}
