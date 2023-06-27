package me.ikevoodoo.smpcore.config;

import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class ConfigUtils {

    public static List<String> getAllValuePaths(ConfigurationSection section, boolean recurse) {
        return getAllValuePathsInternal(section.getCurrentPath(), section, recurse);
    }

    private static List<String> getAllValuePathsInternal(String base, ConfigurationSection section, boolean recurse) {
        var out = new ArrayList<String>();

        var atBase = base.equals(section.getCurrentPath());

        for (var key : section.getKeys(false)) {
            var sub = section.getConfigurationSection(key);
            if (sub != null) {
                if (recurse) {
                    out.addAll(getAllValuePathsInternal(base, sub, true));
                }

                continue;
            }

            if (atBase) {
                out.add(key);
                continue;
            }

            out.add((section.getCurrentPath() + "." + key)
                    .replaceAll("^" + Matcher.quoteReplacement(base) + "\\.?", "")
                    .replaceAll("\\.$", ""));
        }

        return out;
    }

}
