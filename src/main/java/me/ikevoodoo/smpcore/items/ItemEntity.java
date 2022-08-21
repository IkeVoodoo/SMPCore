package me.ikevoodoo.smpcore.items;

import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class ItemEntity {

    private final ItemStack stack;
    private Location location;

    private boolean gravity = true;
    private boolean glowing = false;
    private boolean invulnerable = false;
    private Vector velocity = null;

    private ItemEntity(ItemStack stack) {
        this.stack = stack;
    }

    public static ItemEntity of(ItemStack stack) {
        return new ItemEntity(stack);
    }

    public ItemEntity setLocation(Location location) {
        this.location = location;
        return this;
    }

    public ItemEntity setGravity(boolean gravity) {
        this.gravity = gravity;
        return this;
    }

    public ItemEntity setGlowing(boolean glowing) {
        this.glowing = glowing;
        return this;
    }

    public ItemEntity setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
        return this;
    }

    public ItemEntity setVelocity(Vector velocity) {
        this.velocity = velocity;
        return this;
    }

    public ItemEntity noVelocity() {
        return this.setVelocity(new Vector(0, 0, 0));
    }

    public void spawn() {
        if (this.location == null) {
            throw new IllegalStateException("Location is not set");
        }

        Item item = this.location.getWorld().dropItem(this.location, this.stack);
        item.setGravity(this.gravity);
        item.setGlowing(this.glowing);
        item.setInvulnerable(this.invulnerable);
        if (this.velocity != null)
            item.setVelocity(this.velocity);
    }
}
