package me.ikevoodoo.smpcore.menus;

import me.ikevoodoo.smpcore.text.messaging.Message;

public class PageData {

    private final int size;
    private final Message title;

    private PageData(int size, Message title) {
        this.size = size;
        this.title = title;
    }

    protected int size() {
        return this.size;
    }

    protected Message title() {
        return this.title;
    }

    public static PageData of(int size, Message title) {
        return new PageData(size, title);
    }

}
