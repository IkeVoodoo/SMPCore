package me.ikevoodoo.smpcore.config2;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.utils.Pair;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;

public class ConfigHandler {

    private final HashMap<String, Pair<Configuration, ?>> configs;
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

    public void registerConfig(Pair<Configuration, ?> config) {
        this.configs.put(config.getFirst().getName(), config);
    }

    @SuppressWarnings("unchecked")
    public <T> T getConfig(String name) {
        var pair = this.configs.get(name);
        if (pair == null) return null;

        return (T) pair.getSecond();
    }

    public <T> T getConfig(Class<T> clazz) {
        return this.getConfig(Configuration.getNameForClass(clazz));
    }

    public <T> Map<String, Object> extractValues(String name, Function<T, Map<String, Object>> extracter) {
        var conf = this.<T>getConfig(name);
        if (conf == null) return Map.of();

        return extracter.apply(conf);
    }

    public <T> Map<String, Object> extractValues(Class<T> clazz, Function<T, Map<String, Object>> extracter) {
        return this.extractValues(Configuration.getNameForClass(clazz), extracter);
    }

    public boolean exists(String name) {
        return this.configs.containsKey(name) && this.getFile(name).exists();
    }

    public FileConfiguration getYmlConfig(String name) {
        return this.yamlConfigs.computeIfAbsent(name, this::loadConfig);
    }

    public File getFile(String name) {
        return new File(this.plugin.getDataFolder(), name);
    }

    public void saveConfig(String name) throws IOException {
        getYmlConfig(name).save(getFile(name));
    }

    public void reload() {
        for (var config : this.configs.values()) {
            try {
                config.getFirst().reload();
            } catch (IOException ignored) {
                this.plugin.getLogger().log(Level.SEVERE, "Unable to reload config {}!", config.getFirst().getName());
            }
        }

        for (var entry : this.yamlConfigs.entrySet()) {
            entry.setValue(loadConfig(entry.getKey()));
        }
    }

    private YamlConfiguration loadConfig(String name) {
        return YamlConfiguration.loadConfiguration(getFile(name));
    }


}
