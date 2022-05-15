package me.ikevoodoo.smpcore.messaging;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class MessageBuilder {

    private List<MessageComponent> messageComponents = new ArrayList<>();

    // Starts a component
    public MessageBuilder add() {
        messageComponents.add(new MessageComponent());
        return this;
    }

    public MessageBuilder text(String text) {
        last().setText(text);
        return this;
    }

    public MessageBuilder color(ChatColor color) {
        last().setColor(color);
        return this;
    }

    public MessageBuilder property(MessageProperty property) {
        last().getProperties().add(property);
        return this;
    }

    public Message build() {
        ComponentBuilder componentBuilder = new ComponentBuilder();
        for (MessageComponent messageComponent : messageComponents) {
            componentBuilder.append(messageComponent.getText())
                    .color(messageComponent.getColor());
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
        return messageComponents.get(messageComponents.size() - 1);
    }

}
