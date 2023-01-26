package me.ikevoodoo.smpcore.utils;

import org.bukkit.Material;
import org.bukkit.inventory.*;

public final class RecipeUtils {

    private RecipeUtils() {

    }

    public static RecipeChoice[] getChoices(Recipe recipe) {
        if (recipe instanceof ShapedRecipe shapedRecipe) {
            return shapedRecipe.getChoiceMap().values().toArray(RecipeChoice[]::new);
        }

        if (recipe instanceof ShapelessRecipe shapelessRecipe) {
            return shapelessRecipe.getChoiceList().toArray(new RecipeChoice[0]);
        }

        return new RecipeChoice[9];
    }

    public static ItemStack getStack(RecipeChoice choice) {
        if (choice instanceof RecipeChoice.ExactChoice exactChoice) {
            return exactChoice.getItemStack();
        }

        if (choice instanceof RecipeChoice.MaterialChoice materialChoice) {
            return materialChoice.getItemStack();
        }

        return new ItemStack(Material.AIR);
    }

}
