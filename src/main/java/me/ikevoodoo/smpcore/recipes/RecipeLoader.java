package me.ikevoodoo.smpcore.recipes;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.items.CustomItem;
import me.ikevoodoo.smpcore.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.persistence.PersistentDataType;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;
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

    private String toReadable(ItemStack stack) {
        var readableType = toReadable(stack.getType());

        var meta = stack.getItemMeta();
        if (meta != null) {
            var pdc = meta.getPersistentDataContainer();
            for (var key : pdc.getKeys()) {
                try {
                    pdc.get(key, PersistentDataType.BYTE); // A hack to filter for type, smh
                    return key.getKey();
                } catch (IllegalArgumentException ignored) {

                }
            }

            readableType += "[";
            var modelData = meta.getCustomModelData();
            if (modelData != 0) {
                readableType += "customModelData=" + modelData;
            }

            readableType += "]";
        }

        return readableType;
    }

    private String toReadable(RecipeChoice choice) {
        if (choice instanceof RecipeChoice.ExactChoice exact)
            return toReadable(exact.getItemStack());

        if (choice instanceof RecipeChoice.MaterialChoice mat)
            return toReadable(mat.getChoices().get(0));

        throw new IllegalArgumentException("Recipe choice is neither an ExactChoice or a MaterialChoice, got " + (choice == null ? null : choice.getClass()));
    }

    private Material fromString(String mat) {
        return Material.getMaterial(fix(mat));
    }

    private ItemStack createStack(Material material, String propertyString) {
        var stack = new ItemStack(material);

        var meta = stack.getItemMeta();
        if (meta == null || propertyString.isEmpty()) return null;

        var properties = propertyString.split(",");
        for (var property : properties) {
            var split = property.split(":");
            var name = split[0];
            var value = split[1];

            switch (name) {
                case "customModelData" -> meta.setCustomModelData(Integer.parseInt(value));
            }
        }

        stack.setItemMeta(meta);

        return stack;
    }

    /**
     * Requires the following format:
     *
     * configuration section:
     *   slot:
     *     item: item id
     *
     * */
    private RecipeChoice[] getChoices(ConfigurationSection config, String path) {
        RecipeChoice[] choices = Arrays.stream(new RecipeChoice[9]).map(m -> new RecipeChoice.MaterialChoice(Material.AIR)).toArray(RecipeChoice[]::new);

        if(!config.isConfigurationSection(path))
            return choices;

        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) return choices;
        for(String key : section.getKeys(false)) {
            Material mat = Material.AIR;
            String matName = config.getString(path + "." + key + ".item");
            if(matName == null)
                continue;

            var propertyStart = matName.indexOf('[');
            var propertyEnd = matName.indexOf(']');

            String propertyString = null;
            String materialString = matName;

            if (propertyStart != -1 && propertyEnd != -1) {
                propertyString = matName.substring(propertyStart + 1, propertyEnd);
                materialString = matName.substring(propertyStart);
            }

            String name = StringUtils.toEnumCompatible(materialString);

            int index = Integer.parseInt(key);

            Optional<CustomItem> itemOptional = plugin.getItem(name.toLowerCase(Locale.ROOT));
            int idx = Math.max(0, Math.min(index - 1, choices.length - 1));
            if (itemOptional.isPresent()) {
                CustomItem item = itemOptional.get();
                ItemStack stack = item.getItemStack();
                if (stack == null) continue;

                choices[idx] = new RecipeChoice.ExactChoice(stack);
                continue;
            }

            try {
                mat = Material.valueOf(fix(name));
            } catch (IllegalArgumentException e) {
                plugin.getLogger().log(Level.SEVERE, "Invalid material: {0}", matName);
            }

            var stack = createStack(mat, propertyString);

            if (stack != null) {
                choices[idx] = new RecipeChoice.ExactChoice(stack);
            } else {
                choices[idx] = new RecipeChoice.MaterialChoice(mat);
            }
        }

        for (int i = 0; i < choices.length; i++) {
            config.set(path + "." + (i + 1) + ".item", toReadable(choices[i]));
        }

        return choices;
    }

    public Material[] getMats(RecipeChoice[] choices) {
        return Arrays.stream(choices).map(choice -> {
            if (choice instanceof RecipeChoice.ExactChoice exact)
                return exact.getItemStack().getType();

            if (choice instanceof RecipeChoice.MaterialChoice mat)
                return mat.getChoices().get(0);

            return Material.AIR;
        }).toArray(Material[]::new);
    }

    public RecipeData getRecipe(ConfigurationSection config, String path, ItemStack output, NamespacedKey key, boolean shaped) {
        RecipeChoice[] choices = getChoices(config, path);
        if(shaped) {
            ShapedRecipe recipe = new ShapedRecipe(key, output);
            recipe.shape("012", "345", "678");
            for(int i = 0; i < choices.length; i++)
                recipe.setIngredient((i + "").charAt(0), choices[i]);
            return new RecipeData(recipe, getMats(choices), choices);
        }

        ShapelessRecipe recipe = new ShapelessRecipe(key, output);
        for(RecipeChoice choice : choices)
            recipe.addIngredient(choice);
        return new RecipeData(recipe, getMats(choices), choices);
    }

    public RecipeData getRecipe(String path, ItemStack output, NamespacedKey key, boolean shaped) {
        return getRecipe(plugin.getConfig(), path, output, key, shaped);
    }

    public RecipeData getRecipe(ConfigurationSection config, String path, ItemStack output, NamespacedKey key, RecipeOptions options) {
        return getRecipe(config, path, output, key, options.shaped());
    }

    public RecipeOptions getOptions(ConfigurationSection config) {
        if(config == null) return null;
        return new RecipeOptions(
                fromString(config.getString("type")),
                config.getInt("outputAmount"),
                config.getBoolean("shaped")
        );
    }

    public RecipeOptions getOptions(String path) {
        return getOptions(plugin.getConfig().getConfigurationSection(path));
    }

    public void writeRecipe(File file, RecipeData recipe, RecipeReplacement... replacements) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
        RecipeChoice[] choices = recipe.choices();
        for(int i = 0; i < choices.length; i++) {
            String key = String.valueOf(i + 1);
            Optional<Object> replacement = getReplacement(key, replacements);
            config.set("recipe." + key + ".item", replacement.orElse(toReadable(choices[i])));
        }
        config.set("options.type", toReadable(recipe.recipe().getResult().getType()));
        config.set("options.shaped", recipe.recipe() instanceof ShapedRecipe);
        config.set("options.outputAmount", recipe.recipe().getResult().getAmount());
        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<Object> getReplacement(String key, RecipeReplacement... replacements) {
        for(RecipeReplacement replacement : replacements) {
            if(replacement.key().equals(key))
                return Optional.ofNullable(replacement.value());
        }
        return Optional.empty();
    }


}
