package me.ikevoodoo.smpcore.text.messaging.utils;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;


public class MessageUtils {

    private MessageUtils() {

    }

    public static TextComponent toTextComponent(BaseComponent... components) {
        TextComponent comp = new TextComponent();
        for (BaseComponent component : components) comp.addExtra(component);
        return comp;
    }

}
