package me.ikevoodoo.smpcore.math.helper;

import org.bukkit.util.Vector;

import java.util.function.Consumer;

public class LineHelper {

    private LineHelper() {

    }

    public static void runOnLine(Vector pointA, Vector pointB, double spacing, Consumer<Vector> consumer) {
        Vector a = new Vector().copy(pointA);
        Vector b = new Vector().copy(pointB);

        Vector middle = b.subtract(a);
        double length = middle.length();
        middle.normalize().multiply(spacing);
        double steps = length / spacing;
        for (int i = 0; i < steps; i++) {
            consumer.accept(a.add(middle));
        }
    }

}
