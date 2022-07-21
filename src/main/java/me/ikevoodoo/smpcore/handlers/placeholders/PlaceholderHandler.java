package me.ikevoodoo.smpcore.handlers.placeholders;

import me.ikevoodoo.smpcore.SMPPlugin;

import java.util.List;

public class PlaceholderHandler {
    /**
     * Create a new PlaceholderBuilder
     *
     * @param prefix The prefix for all of the placeholders
     * @param version The placeholder version
     *
     * @return A new PlaceholderBuilder
     * */
    public static PlaceholderBuilder create(SMPPlugin plugin, String prefix, String version) {
        return new PlaceholderBuilder(getAuthor(plugin), prefix, version);
    }

    private static String getAuthor(SMPPlugin plugin) {
        List<String> authors = plugin.getDescription().getAuthors();
        return authors.isEmpty() ? "SMPCore" : authors.get(0);
    }
}
