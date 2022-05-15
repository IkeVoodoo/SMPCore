package me.ikevoodoo.smpcore.utils;

import me.ikevoodoo.smpcore.SMPPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Level;

public class RecipeLoader {
    private static final String[] MATERIAL_NAMES = StringUtils.toStringArray(Material.values());

    private final SMPPlugin plugin;


    public RecipeLoader(SMPPlugin plugin) {
        this.plugin = plugin;
    }

    private String closestMaterial(String name) {
        return StringUtils.getClosest(name, MATERIAL_NAMES);
    }

    private String fix(String mat) {
        return closestMaterial(
                StringUtils.toEnumCompatible(mat)
        );
    }

    private String toReadable(Material mat) {
        return mat.toString()
                .toLowerCase(Locale.ROOT)
                .replaceAll("([a-zA-Z0-9])_", "$1 ")
                .replaceAll("-+", "_")
                .replace("minecraft:", "")
                .replace(":", "");
    }

    /**
     * Requires the following format:
     *
     * configuration section:
     *   slot:
     *     item: item id
     *
     * */
    private Material[] getMaterials(FileConfiguration config, String path) {
        Material[] materials = Arrays.stream(new Material[9]).map(m -> Material.AIR).toArray(Material[]::new);

        if(!config.isConfigurationSection(path)) {
            return materials;
        }

        for(String key : config.getConfigurationSection(path).getKeys(false)) {
            Material mat = Material.AIR;
            String matName = config.getString(path + "." + key + ".item");
            if(matName == null)
                continue;
            try {
                mat = Material.valueOf(fix(matName));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().log(Level.SEVERE, "Invalid material: {0}", matName);
            }
            int index = Integer.parseInt(key);
            if(index > 0 && index <= materials.length) materials[index - 1] = mat;
            else materials[index] = mat;
        }

        for (int i = 0; i < materials.length; i++) {
            config.set(path + "." + (i + 1) + ".item", toReadable(materials[i]));
        }

        return materials;
    }

    public Recipe getRecipe(FileConfiguration config, String path, ItemStack output, NamespacedKey key, boolean shaped) {
        Material[] materials = getMaterials(config, path);
        if(shaped) {
            ShapedRecipe recipe = new ShapedRecipe(key, output);
            recipe.shape("012", "345", "678");
            for(int i = 0; i < materials.length; i++)
                recipe.setIngredient((i + "").charAt(0), materials[i]);
            return recipe;
        }

        ShapelessRecipe recipe = new ShapelessRecipe(key, output);
        for(Material mat : materials)
            recipe.addIngredient(mat);
        return recipe;
    }

    public Recipe getRecipe(String path, ItemStack output, NamespacedKey key, boolean shaped) {
        return getRecipe(plugin.getConfig(), path, output, key, shaped);
    }


}
