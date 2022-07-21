package me.ikevoodoo.smpcore.menus;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.NamespacedKey;

import java.util.HashMap;
import java.util.Locale;
import java.util.Optional;

public abstract class MenuHolder extends PluginProvider {

    private final HashMap<NamespacedKey, Menu> menus = new HashMap<>();
    private Menu parent;

    MenuHolder(SMPPlugin plugin) {
        super(plugin);
    }

    public final MenuHolder add(Menu menu) {
        if (menu == this) return this;
        if (!menu.id().getKey().equals(getPlugin().getName().toLowerCase(Locale.ROOT))) return this;
        menus.put(menu.id(), menu);
        if (this instanceof Menu m)
            menu.setParent(m);
        return this;
    }

    protected final MenuHolder setParent(Menu parent) {
        this.parent = parent;
        return this;
    }

    public final Menu getParent() {
        return this.parent;
    }

    public final Optional<Menu> get(NamespacedKey id) {
        return Optional.ofNullable(this.menus.get(id));
    }

    public final Optional<Menu> get(String id) {
        return get(makeKey(id));
    }
}
