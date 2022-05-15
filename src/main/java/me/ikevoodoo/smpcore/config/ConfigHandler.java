package me.ikevoodoo.smpcore.config;

import java.util.HashMap;

public class ConfigHandler {

    private final HashMap<String, ConfigData> configs;

    public ConfigHandler() {
        configs = new HashMap<>();
    }

    public void registerConfig(ConfigData config) {
        configs.put(config.getName(), config);
    }

    public ConfigData getConfig(String name) {
        return configs.get(name);
    }

    public void reload() {
        for (ConfigData config : configs.values()) {
            config.reload();
        }
    }

}
