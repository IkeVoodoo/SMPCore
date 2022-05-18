package me.ikevoodoo.smpcore.messaging;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageBuilder {

    private List<MessageComponent> messageComponents = new ArrayList<>();

    // Starts a component
    public MessageBuilder add() {
        messageComponents.add(new MessageComponent());
        return this;
    }

    public MessageBuilder add(String text) {
        return add().text(text);
    }

    public MessageBuilder text(String text) {
        last().setText(text);
        return this;
    }

    public MessageBuilder color(ChatColor color) {
        last().setColor(color);
        return this;
    }

    public MessageBuilder properties(MessageProperty... properties) {
        Collections.addAll(last().getProperties(), properties);
        return this;
    }
    
    public MessageBuilder click(ClickEvent.Action action, String data) {
        last().setClickEvent(new ClickEvent(action, data));
        return this;
    }

    public MessageBuilder hover(HoverEvent.Action action, Content... contents) {
        last().setHoverEvent(new HoverEvent(action, contents));
        return this;
    }

    public Message build() {
        // https://www.spigotmc.org/wiki/the-chat-component-api/
        ComponentBuilder componentBuilder = new ComponentBuilder();
        for (MessageComponent messageComponent : messageComponents) {
            componentBuilder
                    .append(messageComponent.getText())
                    .color(messageComponent.getColor())
                    .event(messageComponent.getClickEvent())
                    .event(messageComponent.getHoverEvent());
            for (MessageProperty property : messageComponent.getProperties()) {
                switch (property) {
                    case BOLD -> componentBuilder.bold(true);
                    case ITALIC -> componentBuilder.italic(true);
                    case STRIKETHROUGH -> componentBuilder.strikethrough(true);
                    case UNDERLINE -> componentBuilder.underlined(true);
                    case OBFUSCATED -> componentBuilder.obfuscated(true);
                }
            }

        }
        return new Message(componentBuilder.create());
    }

    private MessageComponent last() {
        if(messageComponents.isEmpty())
            throw new IllegalStateException("No components added! Did you forget to call MessageBuilder#add()?");
        return messageComponents.get(messageComponents.size() - 1);
    }

}
