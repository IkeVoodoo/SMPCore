package me.ikevoodoo.smpcore.players;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class FakePlayer {

    private final String name;
    private final FakeCommandSender fakeCommandSender;

    public FakePlayer(String name) {
        this.name = name;
        fakeCommandSender = new FakeCommandSender(this);
    }

    public String getName() {
        return name;
    }

    public void addMessageHandler(FakePlayerMessageHandler handler) {
        fakeCommandSender.addMessageHandler(handler);
    }

    public void removeMessageHandler(FakePlayerMessageHandler handler) {
        fakeCommandSender.removeMessageHandler(handler);
    }

    public void sendMessage(String... messages) {
        fakeCommandSender.sendMessage(messages);
    }

    public void sendMessage(UUID sender, String... messages) {
        fakeCommandSender.sendMessage(sender, messages);
    }

    public FakeCommandSender getFakeCommandSender() {
        return fakeCommandSender;
    }
}


class FakeCommandSender implements CommandSender {

    private final FakePlayer player;
    private final List<PermissionAttachment> attachments = new ArrayList<>();
    private final List<FakePlayerMessageHandler> messageHandlers = new ArrayList<>();

    private boolean isOp = false;


    public FakeCommandSender(FakePlayer player) {
        this.player = player;
    }

    public void addMessageHandler(FakePlayerMessageHandler handler) {
        messageHandlers.add(handler);
    }

    public void removeMessageHandler(FakePlayerMessageHandler handler) {
        messageHandlers.remove(handler);
    }

    @Override
    public void sendMessage(String message) {
        for (FakePlayerMessageHandler handler : messageHandlers) {
            handler.onMessage(player, message);
        }
    }

    @Override
    public void sendMessage(String... messages) {
        for(FakePlayerMessageHandler handler : messageHandlers) {
            handler.onMessage(player, messages);
        }
    }

    @Override
    public void sendMessage(UUID sender, String message) {
        for(FakePlayerMessageHandler handler : messageHandlers) {
            handler.onMessage(player, sender, message);
        }
    }

    @Override
    public void sendMessage(UUID sender, String... messages) {
        for(FakePlayerMessageHandler handler : messageHandlers) {
            handler.onMessage(player, sender, messages);
        }
    }

    @Override
    public Server getServer() {
        return Bukkit.getServer();
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public Spigot spigot() {
        return null;
    }

    @Override
    public boolean isPermissionSet(String name) {
        return false;
    }

    @Override
    public boolean isPermissionSet(Permission perm) {
        return false;
    }

    @Override
    public boolean hasPermission(String name) {
        for (PermissionAttachment attachment : attachments) {
            for(Map.Entry<String, Boolean> perm : attachment.getPermissions().entrySet()) {
                if (perm.getKey().equals(name)) {
                    return perm.getValue();
                }
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return hasPermission(perm.getName());
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        PermissionAttachment attachment = new PermissionAttachment(plugin, this);
        attachment.setPermission(name, value);
        attachments.add(attachment);
        return attachment;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return null;
    }

    @Override
    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return null;
    }

    @Override
    public void removeAttachment(PermissionAttachment attachment) {
        attachments.remove(attachment);
    }

    @Override
    public void recalculatePermissions() {

    }

    @Override
    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        Set<PermissionAttachmentInfo> permissions = new HashSet<>();
        for (PermissionAttachment attachment : attachments) {
            for(Map.Entry<String, Boolean> perm : attachment.getPermissions().entrySet()) {
                permissions.add(new PermissionAttachmentInfo(this, perm.getKey(), attachment, perm.getValue()));
            }
        }
        return permissions;
    }

    @Override
    public boolean isOp() {
        return this.isOp;
    }

    @Override
    public void setOp(boolean value) {
        this.isOp = value;
    }
}