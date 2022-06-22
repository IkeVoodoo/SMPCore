package me.ikevoodoo.smpcore.utils;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

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
            return maxHealth.getValue();
        return 20;
    }

    public static boolean setIfWithin(double amount, double min, double max, LivingEntity entity) {
        if(amount > min && amount <= max) {
            set(amount, entity);
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
        return setIfWithin(get(entity) + amount, 0, max, entity);
    }

    public static boolean decreaseIfOver(double amount, double min, LivingEntity entity) {
        return setIfWithin(get(entity) - amount, min, 2048, entity);
    }

    public static void heal(Player player, double amount) {
        double max = get(player);
        double health = player.getHealth() + amount;
        if(health > max) health = max;
        player.setHealth(health);
    }

}
