package me.ikevoodoo.smpcore.text.messaging;

import net.md_5.bungee.api.chat.BaseComponent;

import java.util.function.Consumer;
import java.util.function.Predicate;

public enum MessageProperty {

    BOLD(BaseComponent::isBold),
    ITALIC(BaseComponent::isItalic),
    STRIKETHROUGH(BaseComponent::isStrikethrough),
    UNDERLINE(BaseComponent::isUnderlined),
    OBFUSCATED(BaseComponent::isObfuscated),
    RESET(comp -> !comp.hasFormatting());

    private static final MessageProperty[] PROPERTIES = MessageProperty.values();

    private final Predicate<BaseComponent> appliedPredicate;

    MessageProperty(Predicate<BaseComponent> appliedPredicate) {
        this.appliedPredicate = appliedPredicate;
    }

    public boolean applies(BaseComponent component) {
        return this.appliedPredicate.test(component);
    }

    public static void forEachApplying(BaseComponent component, Consumer<MessageProperty> propertyConsumer) {
        for (var property : PROPERTIES) {
            if (!property.applies(component)) continue;

            propertyConsumer.accept(property);
        }
    }


    public static MessageProperty fromString(String s) {
        if (s == null) {
            return null;
        }
        try {
            return valueOf(s.toUpperCase());
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

}
