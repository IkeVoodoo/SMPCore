package me.ikevoodoo.smpcore.menus.v2;

import me.ikevoodoo.smpcore.menus.v2.widgets.Widget;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

public abstract class Page {

    private final List<BiFunction<Inventory, Player, Widget>> widgetSuppliers = new ArrayList<>();

    private final int index;
    private final int width;
    private final int height;
    private final PagedMenu menu;

    protected Page(int index, int width, int height, PagedMenu menu) {
        this.index = index;
        this.width = width;
        this.height = height;
        this.menu = menu;
    }

    public final void update(Inventory inventory, Player player) {
        this.draw(inventory, player);

        for (var widgetSupplier : this.widgetSuppliers) {
            var widget = widgetSupplier.apply(inventory, player);
            if (widget == null) continue;

            if (!widget.canDisplay()) continue;

            widget.draw(inventory, player);
        }
    }

    protected abstract void draw(Inventory inventory, Player player);

    protected final void setBackground(Material background) {

    }

    protected final void addWidgetWhen(Widget widget, BiPredicate<Inventory, Player> predicate) {
        this.widgetSuppliers.add((inv, player) -> {
            if (predicate.test(inv, player)) {
                return widget;
            }

            return null;
        });
    }

    protected final void addWidget(Widget widget) {
        this.addWidgetWhen(widget, (inv, player) -> true);
    }

    public int getIndex() {
        return index;
    }

    public boolean isFirst() {
        return this.getIndex() == 0;
    }

    public boolean isLast() {
        return this.getIndex() == this.getMenu().getPageCount();
    }

    public PagedMenu getMenu() {
        return menu;
    }
}
