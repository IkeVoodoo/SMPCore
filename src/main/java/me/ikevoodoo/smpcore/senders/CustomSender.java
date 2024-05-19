package me.ikevoodoo.smpcore.senders;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class CustomSender implements CommandSender {

    private CustomSender() {

    }

    public static CustomSender as() {
        return new CustomSender();
    }

    private CommandSender sender;
    private boolean log;

    public CustomSender sender(CommandSender sender) {
        this.sender = sender;
        return this;
    }

    public CustomSender console() {
        return this.sender(Bukkit.getConsoleSender());
    }

    public CustomSender player(Player player) {
        return this.sender(player);
    }

    public CustomSender player(String player) {
        return this.player(Bukkit.getPlayer(player));
    }

    public CustomSender player(UUID uuid) {
        return this.player(Bukkit.getPlayer(uuid));
    }

    public CustomSender log(boolean log) {
        this.log = log;
        return this;
    }

    /**
     * @see #log(boolean)
     * */
    public CustomSender silent() {
        return this.log(false);
    }

    public boolean shouldLog() {
        return log;
    }

    @Override
    public void sendMessage(@NotNull String message) {
        if (!this.shouldLog()) return;

        this.sender.sendMessage(message);
    }

    @Override
    public void sendMessage(String... messages) {
        if (!this.shouldLog()) return;

        this.sender.sendMessage(messages);
    }

    @Override
    public void sendMessage(UUID s1, @NotNull String message) {
        if (!this.shouldLog()) return;

        this.sender.sendMessage(s1, message);
    }

    @Override
    public void sendMessage(UUID s1, String... messages) {
        if (!this.shouldLog()) return;

        this.sender.sendMessage(s1, messages);
    }

    @Override
    public @NotNull Server getServer() {
        return this.sender.getServer();
    }

    @Override
    public @NotNull String getName() {
        return this.sender.getName();
    }

    @Override
    public @NotNull Spigot spigot() {
        return this.sender.spigot();
    }

    @Override
    public boolean isPermissionSet(@NotNull String name) {
        return this.sender.isPermissionSet(name);
    }

    @Override
    public boolean isPermissionSet(@NotNull Permission perm) {
        return this.sender.isPermissionSet(perm);
    }

    @Override
    public boolean hasPermission(@NotNull String name) {
        return this.sender.hasPermission(name);
    }

    @Override
    public boolean hasPermission(@NotNull Permission perm) {
        return this.sender.hasPermission(perm);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
        return this.sender.addAttachment(plugin, name, value);
    }

    @Override
    public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
        return this.sender.addAttachment(plugin);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
        return this.sender.addAttachment(plugin, name, value, ticks);
    }

    @Override
    public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
        return this.sender.addAttachment(plugin, ticks);
    }

    @Override
    public void removeAttachment(@NotNull PermissionAttachment attachment) {
        this.sender.removeAttachment(attachment);
    }

    @Override
    public void recalculatePermissions() {
        this.sender.recalculatePermissions();
    }

    @Override
    public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return this.sender.getEffectivePermissions();
    }

    @Override
    public boolean isOp() {
        return this.sender.isOp();
    }

    @Override
    public void setOp(boolean value) {
        this.sender.setOp(value);
    }
}
