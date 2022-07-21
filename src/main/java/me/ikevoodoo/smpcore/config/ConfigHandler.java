package me.ikevoodoo.smpcore.config;

import me.ikevoodoo.smpcore.SMPPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class ConfigHandler {

    private final HashMap<String, ConfigData> configs;
    private final HashMap<String, YamlConfiguration> yamlConfigs;
    private final SMPPlugin plugin;

    public ConfigHandler(SMPPlugin plugin) {
        configs = new HashMap<>();
        yamlConfigs = new HashMap<>();
        this.plugin = plugin;
    }

    public void registerConfig(ConfigData config) {
        configs.put(config.getName(), config);
    }

    public ConfigData getConfig(String name) {
        return configs.get(name);
    }

    public boolean exists(String name) {
        return this.configs.containsKey(name) && new File(plugin.getDataFolder(), name).exists();
    }

    public FileConfiguration getYmlConfig(String name) {
        ConfigData config = getConfig(name);
        if (config == null)
            return yamlConfigs.computeIfAbsent(name, s -> YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), name)));
        return config.getConfig();
    }

    public File getFile(String name) {
        return new File(plugin.getDataFolder(), name);
    }

    public void reload() {
        for (ConfigData config : configs.values()) {
            config.reload();
        }

        // Lazy reload of yaml configs
        yamlConfigs.clear();
    }

}
