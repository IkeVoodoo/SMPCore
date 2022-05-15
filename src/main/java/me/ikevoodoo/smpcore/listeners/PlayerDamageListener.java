package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.events.PlayerPreDeathEvent;
import me.ikevoodoo.smpcore.events.TotemCheckEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
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
                || event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE
                || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION
                || event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK) return;

        if(check(event.getEntity(), event.getFinalDamage(), null))
            event.setCancelled(true);
    }

    private boolean check(Entity entity, double finalDamage, Entity damager) {
        if(!(entity instanceof Player player))
            return false;

        TotemCheckEvent totemCheckEvent = new TotemCheckEvent(player);
        Bukkit.getPluginManager().callEvent(totemCheckEvent);

        if(totemCheckEvent.hasTotem() || player.getHealth() - finalDamage > 0)
            return false;

        PlayerPreDeathEvent playerPreDeathEvent = new PlayerPreDeathEvent(player, damager);
        Bukkit.getPluginManager().callEvent(playerPreDeathEvent);

        return playerPreDeathEvent.isCancelled();
    }
    
}
