package me.ikevoodoo.smpcore.menus.functional;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.functional.loop.FunctionalLoopBase;
import me.ikevoodoo.smpcore.menus.Menu;
import me.ikevoodoo.smpcore.menus.PageData;
import me.ikevoodoo.smpcore.shared.PluginProvider;
import org.bukkit.NamespacedKey;

public class FunctionalMenu extends PluginProvider implements FunctionalLoopBase {

    private Menu menu;

    protected FunctionalMenu(SMPPlugin plugin) {
        super(plugin);
    }

    public FunctionalMenu id(NamespacedKey key) {
        this.menu = new Menu(this.getPlugin(), key);
        return this;
    }

    public FunctionalMenu id(String key) {
        return this.id(new NamespacedKey(this.getPlugin(), key));
    }

    public FunctionalPage page(PageData data) {
        if (this.menu == null)
            throw new IllegalStateException("Cannot add a page to a null menu!");
        return new FunctionalPage(getPlugin(), this, this.menu.page(data));
    }

    public FunctionalMenu add(Menu menu) {
        this.menu.add(menu);
        return this;
    }

    public FunctionalMenu add(FunctionalMenu menu) {
        return this.add(menu.register());
    }

    public Menu register() {
        getPlugin().getMenuHandler().add(this.menu);
        return this.menu;
    }

}
