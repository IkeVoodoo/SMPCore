package me.ikevoodoo.smpcore.senders;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.UUID;

public class SenderBuilder {
    
    public static CommandSender createNewSender(CustomSender sender) {
        CommandSender s = sender.getSender();
        if(s == null)
            s = Bukkit.getConsoleSender();
        CommandSender finalS = s;
        return new CommandSender() {
            @Override
            public void sendMessage(@NotNull String message) {
                if(sender.isLog())
                    finalS.sendMessage(message);
            }

            @Override
            public void sendMessage(String... messages) {
                if (sender.isLog())
                    finalS.sendMessage(messages);
            }

            @Override
            public void sendMessage(UUID s1, @NotNull String message) {
                if (sender.isLog())
                    finalS.sendMessage(s1, message);
            }

            @Override
            public void sendMessage(UUID s1, String... messages) {
                if (sender.isLog())
                    finalS.sendMessage(s1, messages);
            }

            @Override
            public @NotNull Server getServer() {
                return finalS.getServer();
            }

            @Override
            public @NotNull String getName() {
                return finalS.getName();
            }

            @Override
            public @NotNull Spigot spigot() {
                return finalS.spigot();
            }

            @Override
            public boolean isPermissionSet(@NotNull String name) {
                return finalS.isPermissionSet(name);
            }

            @Override
            public boolean isPermissionSet(@NotNull Permission perm) {
                return finalS.isPermissionSet(perm);
            }

            @Override
            public boolean hasPermission(@NotNull String name) {
                return finalS.hasPermission(name);
            }

            @Override
            public boolean hasPermission(@NotNull Permission perm) {
                return finalS.hasPermission(perm);
            }

            @Override
            public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value) {
                return finalS.addAttachment(plugin, name, value);
            }

            @Override
            public @NotNull PermissionAttachment addAttachment(@NotNull Plugin plugin) {
                return finalS.addAttachment(plugin);
            }

            @Override
            public PermissionAttachment addAttachment(@NotNull Plugin plugin, @NotNull String name, boolean value, int ticks) {
                return finalS.addAttachment(plugin, name, value, ticks);
            }

            @Override
            public PermissionAttachment addAttachment(@NotNull Plugin plugin, int ticks) {
                return finalS.addAttachment(plugin, ticks);
            }

            @Override
            public void removeAttachment(@NotNull PermissionAttachment attachment) {
                finalS.removeAttachment(attachment);
            }

            @Override
            public void recalculatePermissions() {
                finalS.recalculatePermissions();
            }

            @Override
            public @NotNull Set<PermissionAttachmentInfo> getEffectivePermissions() {
                return finalS.getEffectivePermissions();
            }

            @Override
            public boolean isOp() {
                return finalS.isOp();
            }

            @Override
            public void setOp(boolean value) {
                finalS.setOp(value);
            }
        };
    }
    
}
