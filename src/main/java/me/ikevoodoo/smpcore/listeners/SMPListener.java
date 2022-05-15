package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

import java.util.HashMap;

public abstract class SMPListener extends PluginProvider implements Listener {

    private final HashMap<Class<? extends Event>, String> perms = new HashMap<>();

    public SMPListener(SMPPlugin plugin) {
        super(plugin);
    }

    public void setRequiredOption(Class<? extends Event> event, String configOption) {
        perms.put(event, configOption);
    }

    public boolean canContinue(Event event) {
        return perms.containsKey(event.getClass()) && getPlugin().getConfig().getBoolean(perms.get(event.getClass()));
    }


}
