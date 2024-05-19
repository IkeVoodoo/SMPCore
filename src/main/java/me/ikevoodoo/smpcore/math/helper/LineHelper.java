package me.ikevoodoo.smpcore.math.helper;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class LineHelper {

    private LineHelper() {

    }

    public static void runOnLine(Vector pointA, Vector pointB, double spacing, Consumer<Vector> consumer) {
        var start = pointA.clone();
        var end = pointB.clone();

        Vector middle = end.subtract(start);
        var length = middle.length();

        middle.normalize().multiply(spacing);

        var steps = length / spacing;
        for (int i = 0; i < steps; i++) {
            consumer.accept(start.add(middle));
        }
    }

    public static void runOnLine(Location pointA, Location pointB, double spacing, Consumer<Location> consumer) {
        var start = pointA.clone();
        var end = pointB.clone();

        var middle = end.subtract(start).toVector();
        var length = middle.length();

        middle.normalize().multiply(spacing);

        var steps = length / spacing;
        for (int i = 0; i < steps; i++) {
            consumer.accept(start.add(middle));
        }
    }



}
