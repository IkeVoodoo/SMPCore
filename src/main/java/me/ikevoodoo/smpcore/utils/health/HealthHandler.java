package me.ikevoodoo.smpcore.utils.health;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;

public interface HealthHandler {

    void setMaxHealth(LivingEntity entity, double maxHealth);
    void setMaxHealth(LivingEntity entity, double maxHealth, World world);
    double getMaxHealth(LivingEntity entity);

}
