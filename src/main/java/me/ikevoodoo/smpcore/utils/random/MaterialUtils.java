package me.ikevoodoo.smpcore.utils.random;

import org.apache.commons.lang.ArrayUtils;
import org.bukkit.Material;

import java.util.Random;

public class MaterialUtils implements RandomBase<Material> {

    private static final Material[] MATERIALS = Material.values();
    private static final Random random = new Random();

    @Override
    public Material random() {
        return MATERIALS[random.nextInt(MATERIALS.length)];
    }

    @Override
    public Material[] random(int amount) {
        if(amount <= 0)
            return new Material[0];
        if(amount == 1)
            return new Material[] { random() };
        Material[] materials = new Material[amount];
        for (int i = 0; i < amount; i++)
            materials[i] = random();
        return materials;
    }

    @Override
    public Material irandom(Material... materials) {
        return materials[random.nextInt(materials.length)];
    }

    @Override
    public Material erandom(Material exclude) {
        if(exclude == null)
            return random();
        while (true) {
            Material material = random();
            if (material != exclude) {
                return material;
            }
        }
    }

    @Override
    public Material orandom(Material... exclude) {
        if(exclude.length == 0)
            return random();
        while (true) {
            Material material = random();
            if (!ArrayUtils.contains(exclude, material)) {
                return material;
            }
        }
    }
}
