package me.ikevoodoo.smpcore.config;

import me.ikevoodoo.smpcore.SMPPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ConfigHandler {

    private final HashMap<String, ConfigData> configs;
    private final HashMap<String, YamlConfiguration> yamlConfigs;
    private final SMPPlugin plugin;

    public ConfigHandler(SMPPlugin plugin) {
        configs = new HashMap<>();
        yamlConfigs = new HashMap<>();
        this.plugin = plugin;
    }

    public List<String> listConfigs() {
        return this.configs.keySet().stream().toList();
    }

    public List<String> listYmlConfigs() {
        return this.yamlConfigs.keySet().stream().toList();
    }

    public void registerConfig(ConfigData config) {
        this.configs.put(config.getName(), config);
    }

    public ConfigData getConfig(String name) {
        return this.configs.get(name);
    }

    public boolean exists(String name) {
        return this.configs.containsKey(name) && this.getFile(name).exists();
    }

    public FileConfiguration getYmlConfig(String name) {
        var config = this.getConfig(name);
        if (config == null) {
            return this.yamlConfigs.computeIfAbsent(name, this::loadConfig);
        }

        return config.getConfig();
    }

    public File getFile(String name) {
        return new File(this.plugin.getDataFolder(), name);
    }

    public void saveConfig(String name) throws IOException {
        getYmlConfig(name).save(getFile(name));
    }

    public void reload() {
        for (var config : this.configs.values()) {
            config.reload();
        }

        for (var entry : this.yamlConfigs.entrySet()) {
            entry.setValue(loadConfig(entry.getKey()));
        }
    }

    private YamlConfiguration loadConfig(String name) {
        return YamlConfiguration.loadConfiguration(getFile(name));
    }


}
