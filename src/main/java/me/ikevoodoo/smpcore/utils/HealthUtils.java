package me.ikevoodoo.smpcore.utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;

public class HealthUtils {

    private HealthUtils() {

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

    public static boolean setIfWithin(double amount, double min, double max, LivingEntity entity, boolean useDef, double def) {
        if(amount >= min && amount <= max) {
            set(amount, entity);
            return true;
        }
        if (useDef) {
            set(def, entity);
            return true;
        }
        return false;
    }

    public static void increase(double amount, LivingEntity entity) {
        set(get(entity) + amount, entity);
    }

    public static void decrease(double amount, LivingEntity entity) {
        set(get(entity) - amount, entity);
    }

    public static boolean increaseIfUnder(double amount, double max, LivingEntity entity) {
        return increaseIfUnder(amount, max, entity, false);
    }

    public static boolean decreaseIfOver(double amount, double min, LivingEntity entity) {
        return decreaseIfOver(amount, min, entity, false);
    }

    public static boolean increaseIfUnder(double amount, double max, LivingEntity entity, boolean setToMax) {
        return setIfWithin(get(entity) + amount, 0, max, entity, setToMax, max);
    }

    public static boolean decreaseIfOver(double amount, double min, LivingEntity entity, boolean setToMin) {
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
