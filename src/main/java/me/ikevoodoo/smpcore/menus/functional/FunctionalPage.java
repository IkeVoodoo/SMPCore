package me.ikevoodoo.smpcore.menus.functional;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.functional.loop.FunctionalLoopBase;
import me.ikevoodoo.smpcore.menus.MenuPage;
import me.ikevoodoo.smpcore.shared.PluginProvider;

import java.util.function.Consumer;

public class FunctionalPage extends PluginProvider implements FunctionalLoopBase {
    private final FunctionalMenu menu;
    private final MenuPage page;

    protected FunctionalPage(SMPPlugin plugin, FunctionalMenu menu, MenuPage page) {
        super(plugin);
        this.menu = menu;
        this.page = page;
    }

    public FunctionalPage edit(Consumer<MenuPage> consumer) {
        consumer.accept(this.page);
        return this;
    }

    public FunctionalMenu done() {
        return this.menu;
    }


}
