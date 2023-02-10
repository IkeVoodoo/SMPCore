package me.ikevoodoo.smpcore.utils.health;

import me.ikevoodoo.smpcore.SMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public final class HealthHelper {

    private final SMPPlugin plugin;
    private HealthHandler healthHandler;

    public HealthHelper(SMPPlugin plugin) {
        this.plugin = plugin;
    }

    public void setHealthHandler(HealthHandler healthHandler) {
        this.healthHandler = healthHandler;
    }

    public double getMaxHealth(LivingEntity entity) {
        if (this.healthHandler == null) return 20.0;

        return this.healthHandler.getMaxHealth(entity);
    }

    public double getMaxHearts(LivingEntity entity) {
        return this.getMaxHealth(entity) / 2;
    }

    public double setMaxHealth(LivingEntity entity, double amount, World world) {
        if (this.healthHandler == null) return 20.0;

        this.healthHandler.setMaxHealth(entity, amount, world);
        return amount;
    }

    public double setMaxHearts(LivingEntity entity, double amount, World world) {
        return this.setMaxHealth(entity, amount * 2, world);
    }

    public double[] setMaxHealthEverywhere(LivingEntity entity, double amount) {
        if (this.healthHandler == null) return new double[0];

        return Bukkit.getWorlds()
                .stream()
                .mapToDouble(world -> this.setMaxHealth(entity, amount, world))
                .toArray();
    }

    public double[] setMaxHeartsEverywhere(LivingEntity entity, double amount) {
        return this.setMaxHealthEverywhere(entity, amount * 2);
    }

    public double setMaxHealth(LivingEntity entity, double amount) {
        if (this.healthHandler == null) return 20.0;

        this.healthHandler.setMaxHealth(entity, amount);
        return amount;
    }

    public double setMaxHearts(LivingEntity entity, double amount) {
        return this.setMaxHealth(entity, amount * 2);
    }

    public double increaseMaxHealth(LivingEntity entity, double amount) {
        return this.setMaxHealth(entity, this.getMaxHealth(entity) + amount);
    }

    public double increaseMaxHearts(LivingEntity entity, double amount) {
        return this.setMaxHearts(entity, this.getMaxHearts(entity) + amount * 2);
    }

    public double decreaseMaxHealth(LivingEntity entity, double amount) {
        return this.increaseMaxHealth(entity, -amount);
    }

    public double decreaseMaxHearts(LivingEntity entity, double amount) {
        return this.increaseMaxHearts(entity, -amount);
    }

    public HealthSetResult increaseMaxHealthIfUnder(LivingEntity entity, double amount, double max) {
        double health = this.getMaxHealth(entity);
        double toSet = health + amount;

        if (toSet > max) {
            return new HealthSetResult(HealthSetResult.ABOVE_MAX, health, health);
        }

        double newHealth = this.setMaxHealth(entity, Math.min(toSet, max));
        return new HealthSetResult(HealthSetResult.OK, health, newHealth);
    }

    public HealthSetResult increaseMaxHeartsIfUnder(LivingEntity entity, double amount, double max) {
        return this.increaseMaxHealthIfUnder(entity, amount * 2, max * 2);
    }

    public HealthSetResult decreaseMaxHealthIfOver(LivingEntity entity, double amount, double min) {
        double health = this.getMaxHealth(entity);
        double val = health - amount;

        if (val < min) {
            return new HealthSetResult(HealthSetResult.BELOW_MIN, health, health);
        }

        double newHealth = this.setMaxHealth(entity, Math.max(val, min));

        return new HealthSetResult(HealthSetResult.OK, health, newHealth);
    }

    public HealthSetResult decreaseMaxHeartsIfOver(LivingEntity entity, double amount, double min) {
        return this.decreaseMaxHealthIfOver(entity, amount * 2, min * 2);
    }

    public double updateHealth(Player player) {
        return this.setMaxHealth(player, this.getMaxHealth(player));
    }
}
