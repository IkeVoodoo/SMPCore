package me.ikevoodoo.smpcore.recipes;

import org.bukkit.Material;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;

import java.util.Arrays;

public record RecipeData(Recipe recipe, Material[] materials, RecipeChoice... choices) {

    @Override
    public boolean equals(Object obj) {
        return obj instanceof RecipeData data && data.recipe.equals(recipe) && Arrays.equals(data.materials, materials);
    }

    @Override
    public int hashCode() {
        return recipe.hashCode() + Arrays.hashCode(materials);
    }

    @Override
    public String toString() {
        return "RecipeData{" +
                "recipe=" + recipe +
                ", materials=" + Arrays.toString(materials) +
                '}';
    }
}
