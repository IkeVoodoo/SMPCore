package me.ikevoodoo.smpcore.utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

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

    public static void set(double amount, LivingEntity entity) {
        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null)
            maxHealth.setBaseValue(amount);
    }

    public static double get(LivingEntity entity) {
        AttributeInstance maxHealth = entity.getAttribute(Attribute.GENERIC_MAX_HEALTH);
        if (maxHealth != null)
            return maxHealth.getBaseValue();
        return 20;
    }

    public static SetResult setIfWithin(double amount, double min, double max, LivingEntity entity, boolean useDef, double def) {
        if (amount > max) {
            return SetResult.outOfBounds(get(entity), amount);
        }

        if (amount < min) {
            return SetResult.outOfBounds(get(entity), amount);
        }

        if(amount >= min && amount <= max) {
            double old = get(entity);
            set(amount, entity);
            return SetResult.within(old, amount);
        }

        if (useDef) {
            double old = get(entity);
            set(def, entity);
            return SetResult.usedDefault(old, def);
        }
        return SetResult.failure();
    }

    public static void increase(double amount, LivingEntity entity) {
        set(get(entity) + amount, entity);
    }

    public static void decrease(double amount, LivingEntity entity) {
        set(get(entity) - amount, entity);
    }

    public static SetResult increaseIfUnder(double amount, double max, LivingEntity entity) {
        return increaseIfUnder(amount, max, entity, false);
    }

    public static SetResult decreaseIfOver(double amount, double min, LivingEntity entity) {
        return decreaseIfOver(amount, min, entity, false);
    }

    public static SetResult increaseIfUnder(double amount, double max, LivingEntity entity, boolean setToMax) {
        return setIfWithin(get(entity) + amount, 0, max, entity, setToMax, max);
    }

    public static SetResult decreaseIfOver(double amount, double min, LivingEntity entity, boolean setToMin) {
        return setIfWithin(get(entity) - amount, min, 2048, entity, setToMin, min);
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
