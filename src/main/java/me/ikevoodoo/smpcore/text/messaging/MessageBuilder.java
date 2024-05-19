package me.ikevoodoo.smpcore.text.messaging;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Content;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MessageBuilder {

    private final List<MessageComponent> messageComponents = new ArrayList<>();

    private MessageBuilder() {

    }

    public static MessageBuilder create() {
        return new MessageBuilder();
    }

    public static Message messageOf(String text) {
        return MessageBuilder.create().add(text).build();
    }

    public static Message messageOf(Message message) {
        return MessageBuilder.create().add(message).build();
    }

    public static MessageBuilder builderOf(String text) {
        return MessageBuilder.create().add(text);
    }

    public static MessageBuilder builderOf(Message message) {
        return MessageBuilder.create().add(message);
    }

    // Starts a component
    public MessageBuilder add() {
        this.messageComponents.add(new MessageComponent());
        return this;
    }

    public MessageBuilder add(String text) {
        return addComponent(new TextComponent(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', text))));
    }

    public MessageBuilder addPlain(String text) {
        return add().text(text);
    }

    public MessageBuilder add(String text, ChatColor color) {
        return addPlain(text).color(color);
    }

    public MessageBuilder add(Message message) {
        message.components().forEach(this::addComponent);
        return this;
    }

    public MessageBuilder text(String text) {
        last().setText(text);
        return this;
    }

    public MessageBuilder text(String text, ChatColor color) {
        return text(text).color(color);
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

    public MessageBuilder link(String url) {
        return click(ClickEvent.Action.OPEN_URL, url);
    }

    public MessageBuilder link(String url, String text) {
        return addPlain(text).link(url);
    }

    public MessageBuilder link(String url, ChatColor color) {
        return click(ClickEvent.Action.OPEN_URL, url).color(color);
    }

    public MessageBuilder link(String url, String text, ChatColor color) {
        return add(text, color).link(url);
    }

    public MessageBuilder clear() {
        this.messageComponents.clear();
        return this;
    }

    public Message build() {
        // https://www.spigotmc.org/wiki/the-chat-component-api/
        var componentBuilder = new ComponentBuilder();
        for (MessageComponent messageComponent : this.messageComponents) {
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
                    case RESET -> componentBuilder.reset();
                    default -> throw new IllegalArgumentException("Unknown property: " + property);
                }
            }

        }
        return new Message(componentBuilder.create());
    }

    private MessageComponent last() {
        if(this.messageComponents.isEmpty())
            throw new IllegalStateException("No components added! Did you forget to call MessageBuilder#add()?");
        return this.messageComponents.get(this.messageComponents.size() - 1);
    }

    private MessageBuilder addComponent(BaseComponent comp) {
        addPlain(comp.toLegacyText())
                .color(comp.getColor());

        var event = comp.getClickEvent();
        if (event != null) {
            click(event.getAction(), event.getValue());
        }

        var hoverEvent = comp.getHoverEvent();
        if (event != null) {
            hover(hoverEvent.getAction(), hoverEvent.getContents().toArray(new Content[0]));
        }

        var properties = last().getProperties();

        MessageProperty.forEachApplying(comp, property -> {
            if (property == MessageProperty.RESET) return;

            properties.add(property);
        });

        return this;
    }

}