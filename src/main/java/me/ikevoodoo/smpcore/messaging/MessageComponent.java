package me.ikevoodoo.smpcore.messaging;


import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class MessageComponent {

    private String text;
    private ChatColor color;
    private final List<MessageProperty> properties = new ArrayList<>();

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public List<MessageProperty> getProperties() {
        return properties;
    }

}
