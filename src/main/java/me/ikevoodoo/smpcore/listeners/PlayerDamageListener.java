package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.events.PlayerPreDeathEvent;
import me.ikevoodoo.smpcore.events.TotemCheckEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

public class PlayerDamageListener implements Listener {

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(EntityDamageByEntityEvent event) {
        // Only cancel if the check is true, otherwise it might un-cancel the event
        if(check(event.getEntity(), event.getFinalDamage(), event.getDamager()))
            event.setCancelled(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void on(EntityDamageEvent event) {
        if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK
                || (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE)
                || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return;

        if(check(event.getEntity(), event.getFinalDamage(), null))
            event.setCancelled(true);
    }

    private boolean check(Entity killed, double finalDamage, Entity killer) {
        if(!(killed instanceof Player killedPlayer))
            return false;

        Entity killerEntity = killer;
        if (killer instanceof Arrow arrow && (arrow.getShooter() instanceof Entity e))
            killerEntity = e;
        if (killer instanceof TNTPrimed primed)
            killerEntity = primed.getSource();

        TotemCheckEvent totemCheckEvent = new TotemCheckEvent(killedPlayer);
        Bukkit.getPluginManager().callEvent(totemCheckEvent);

        if(totemCheckEvent.hasTotem() || killedPlayer.getHealth() - finalDamage > 0)
            return false;

        PlayerPreDeathEvent playerPreDeathEvent = new PlayerPreDeathEvent(killedPlayer, killerEntity);
        Bukkit.getPluginManager().callEvent(playerPreDeathEvent);

        return playerPreDeathEvent.isCancelled();
    }
    
}
