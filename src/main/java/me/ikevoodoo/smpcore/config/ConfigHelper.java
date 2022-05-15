package me.ikevoodoo.smpcore.config;

import me.ikevoodoo.smpcore.SMPPlugin;

public class ConfigHelper {

    private final SMPPlugin plugin;

    public ConfigHelper(SMPPlugin plugin) {
        this.plugin = plugin;
    }

    public void runIfTrue(String path, Runnable ifTrue, Runnable ifFalse) {
        if (plugin.getConfig().getBoolean(path)) {
            ifTrue.run();
            return;
        }

        ifFalse.run();
    }

}
