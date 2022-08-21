package me.ikevoodoo.smpcore.recipes;

public record RecipeReplacement(String key, Object value) {

    public static RecipeReplacement of(String key, Object value) {
        return new RecipeReplacement(key, value);
    }

    public static RecipeReplacement of(Object key, Object value) {
        return new RecipeReplacement(String.valueOf(key), value);
    }

}
