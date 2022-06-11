package me.ikevoodoo.smpcore.players;

import java.util.UUID;

public interface FakePlayerMessageHandler {

    default void onMessage(FakePlayer player,  String... messages) {
    }

    default void onMessage(FakePlayer player,  UUID sender, String... messages) {
    }

}
