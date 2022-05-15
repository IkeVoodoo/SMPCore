package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.events.DayPassEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;

public class PlayerSleepListener implements Listener {

    @EventHandler
    public void on(TimeSkipEvent event) {
        long currentTime = event.getWorld().getTime();
        long previousTime = currentTime - event.getSkipAmount();
        if(previousTime > 12542 && previousTime < 23000) {
            if(currentTime > 23961 || currentTime < 12542) {
                DayPassEvent dayPassEvent = new DayPassEvent(event.getWorld());
                Bukkit.getPluginManager().callEvent(dayPassEvent);
                event.setCancelled(dayPassEvent.isCancelled());
            }
        }
    }
}
