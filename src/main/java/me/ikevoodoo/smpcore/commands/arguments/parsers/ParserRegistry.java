package me.ikevoodoo.smpcore.commands.arguments.parsers;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class ParserRegistry {

    private static final HashMap<Class<?>, BaseParser<?>> parsers = new HashMap<>();

    static {
        register(new UUIDParser(), UUID.class);
        register(new PlayerParser(), Player.class);
        register(new OfflinePlayerParser(), OfflinePlayer.class);
        register(new IntParser(), int.class, Integer.class);
        register(new DoubleParser(), double.class, Double.class);
        register(new FloatParser(), float.class, Float.class);
        register(new LongParser(), long.class, Long.class);
        register(new BooleanParser(), boolean.class, Boolean.class);
        register(new StringParser(), String.class);
        register(new WorldParser(), World.class);
    }

    private ParserRegistry() {
    }

    public static void register(BaseParser<?> parser, Class<?>... classes) {
        for (Class<?> clazz : classes)
            parsers.put(clazz, parser);
    }

    public static <T> BaseParser<T> get(Class<T> clazz) {
        return (BaseParser<T>) parsers.get(clazz);
    }

    public static boolean has(Class<?> clazz) {
        return parsers.containsKey(clazz);
    }
}
