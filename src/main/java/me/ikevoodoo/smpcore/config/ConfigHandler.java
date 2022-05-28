package me.ikevoodoo.smpcore.config;

import me.ikevoodoo.smpcore.SMPPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;

public class ConfigHandler {

    private final HashMap<String, ConfigData> configs;
    private final SMPPlugin plugin;

    public ConfigHandler(SMPPlugin plugin) {
        configs = new HashMap<>();
        this.plugin = plugin;
    }

    public void registerConfig(ConfigData config) {
        configs.put(config.getName(), config);
    }

    public ConfigData getConfig(String name) {
        return configs.get(name);
    }

    public FileConfiguration getYmlConfig(String name) {
        ConfigData config = getConfig(name);
        if (config == null) {
            return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), name));
        }
        return config.getConfig();
    }

    public void reload() {
        for (ConfigData config : configs.values()) {
            config.reload();
        }
    }

}
