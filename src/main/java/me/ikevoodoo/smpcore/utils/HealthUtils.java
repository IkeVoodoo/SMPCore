package me.ikevoodoo.smpcore.utils;

import me.ikevoodoo.smpcore.SMPPlugin;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;
import java.util.function.Predicate;

public class HealthUtils {

    private HealthUtils() {

    }

    public static class SetResult {
        private final boolean within;
        private final boolean usedDefault;
        private final boolean outOfBounds;
        private final double oldHealth;
        private final double newHealth;

        private SetResult(boolean within, boolean usedDefault, boolean outOfBounds, double oldHealth, double newHealth) {
            this.within = within;
            this.usedDefault = usedDefault;
            this.outOfBounds = outOfBounds;
            this.oldHealth = oldHealth;
            this.newHealth = newHealth;
        }

        public static SetResult within(double oldHealth, double newHealth) {
            return new SetResult(true, false, false, oldHealth, newHealth);
        }

        public static SetResult usedDefault(double oldHealth, double newHealth) {
            return new SetResult(false, true, false, oldHealth, newHealth);
        }

        public static SetResult outOfBounds(double oldHealth, double newHealth) {
            return new SetResult(false, false, true, oldHealth, newHealth);
        }

        public static SetResult failure() {
            return new SetResult(false, false, false, 0, 0);
        }

        public boolean isWithin() {
            return within;
        }

        public boolean hasUsedDefault() {
            return usedDefault;
        }

        public boolean isOutOfBounds() {
            return outOfBounds;
        }

        public boolean hasFailed() {
            return !within && !usedDefault && !outOfBounds;
        }

        public double getOldHealth() {
            return oldHealth;
        }

        public double getNewHealth() {
            return newHealth;
        }
    }

    public static void apply(LivingEntity entity) {
        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null)
            maxHealth.setBaseValue(get(entity));
    }

    public static void reset(LivingEntity entity) {
        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null)
            maxHealth.setBaseValue(maxHealth.getDefaultValue());
    }

    public static void set(double amount, LivingEntity entity, SMPPlugin plugin, World world) {
        NamespacedKey key = plugin.makeKey(world.getUID().toString());
        PersistentDataContainer pdc = entity.getPersistentDataContainer();
        pdc.set(key, PersistentDataType.DOUBLE, amount);
        apply(entity);
    }

    public static void set(double amount, LivingEntity entity, SMPPlugin plugin) {
        set(amount, entity, plugin, entity.getWorld());
    }

    public static void setAll(double amount, LivingEntity entity, SMPPlugin plugin, Predicate<World> predicate) {
        for (World world : Bukkit.getWorlds()) {
            if (predicate.test(world)) {
                set(amount, entity, plugin, world);
            }
        }
    }

    public static void setAll(double amount, LivingEntity entity, SMPPlugin plugin) {
        setAll(amount, entity, plugin, world -> true);
    }

    public static double get(LivingEntity entity) {
        return get(entity, entity.getWorld());
    }

    public static double get(LivingEntity entity, World world) {
        Optional<Pair<String, Double>> amount = PDCUtils.getPartial(entity.getPersistentDataContainer(), world.getUID().toString(), PersistentDataType.DOUBLE);
        if (amount.isPresent())
            return amount.get().getSecond();
        AttributeInstance instance = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        return instance == null ? 20 : instance.getBaseValue();
    }

    public static SetResult setIfWithin(double amount, double min, double max, LivingEntity entity, boolean useDef, double def, SMPPlugin plugin) {
        if (amount > max) {
            return SetResult.outOfBounds(get(entity), amount);
        }

        if (amount < min) {
            return SetResult.outOfBounds(get(entity), amount);
        }

        if(amount >= min && amount <= max) {
            double old = get(entity);
            set(amount, entity, plugin);
            return SetResult.within(old, amount);
        }

        if (useDef) {
            double old = get(entity);
            set(def, entity, plugin);
            return SetResult.usedDefault(old, def);
        }
        return SetResult.failure();
    }

    public static void increase(double amount, LivingEntity entity, SMPPlugin plugin) {
        set(get(entity) + amount, entity, plugin);
    }

    public static void decrease(double amount, LivingEntity entity, SMPPlugin plugin) {
        set(get(entity) - amount, entity, plugin);
    }

    public static SetResult increaseIfUnder(double amount, double max, LivingEntity entity, SMPPlugin plugin) {
        return increaseIfUnder(amount, max, entity, false, plugin);
    }

    public static SetResult decreaseIfOver(double amount, double min, LivingEntity entity, SMPPlugin plugin) {
        return decreaseIfOver(amount, min, entity, false, plugin);
    }

    public static SetResult increaseIfUnder(double amount, double max, LivingEntity entity, boolean setToMax, SMPPlugin plugin) {
        return setIfWithin(get(entity) + amount, 0, max, entity, setToMax, max, plugin);
    }

    public static SetResult decreaseIfOver(double amount, double min, LivingEntity entity, boolean setToMin, SMPPlugin plugin) {
        return setIfWithin(get(entity) - amount, min, 2048, entity, setToMin, min, plugin);
    }

    public static void heal(LivingEntity entity, double amount) {
        double max = get(entity);
        double health = entity.getHealth() + amount;
        if(health > max) health = max;
        if (health < 0) health = 0;
        entity.setHealth(health);
    }

    public static void damage(LivingEntity entity, double amount) {
        heal(entity, -amount);
    }

}
