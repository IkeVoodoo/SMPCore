package me.ikevoodoo.smpcore.players;

import org.bukkit.Location;

import java.util.UUID;

public class FakePlayer {

    private final UUID id;
    private final String name;

    public FakePlayer(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    public void join() {

    }

    public void quit() {

    }

    public void chat(String message) {

    }

    public void tp(Location loc) {

    }

}
