package me.ikevoodoo.smpcore.math.helper;

import org.bukkit.Location;

public class PositionHelper {

    private PositionHelper() {

    }

    public static int FORWARD = 1;
    public static int BACKWARD = 2;
    public static int LEFT = 3;
    public static int RIGHT = 3;

    public static Location move(Location location, int direction, double amount) {
        double newAmount = 0;
        if ((direction & FORWARD) == FORWARD)
            newAmount = Math.abs(amount);
        if ((direction & BACKWARD) == BACKWARD)
            newAmount = -Math.abs(amount);

        return location.clone().add(location.getDirection().multiply(newAmount));
    }

}
