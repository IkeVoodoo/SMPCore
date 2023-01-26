package me.ikevoodoo.smpcore.menus.v2;

import java.util.ArrayList;
import java.util.List;

public abstract class PagedMenu {

    private final List<Page> pages = new ArrayList<>();

    public PagedMenu() {

    }

    public int getPageCount() {
        return this.pages.size();
    }

}
