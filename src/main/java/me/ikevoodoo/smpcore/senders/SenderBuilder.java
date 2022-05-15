package me.ikevoodoo.smpcore.senders;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

import java.util.Set;
import java.util.UUID;

import static me.ikevoodoo.smpcore.senders.CustomSender.as;

public class SenderBuilder {

    public static void main(String[] args) {
        Bukkit.dispatchCommand(createNewSender(as().noLog().console()), "say hi");
    }
    
    public static CommandSender createNewSender(CustomSender sender) {
        CommandSender s = sender.getSender();
        if(s == null)
            s = Bukkit.getConsoleSender();
        CommandSender finalS = s;
        return new CommandSender() {
            @Override
            public void sendMessage(String message) {
                if(sender.isLog())
                    finalS.sendMessage(message);
            }

            @Override
            public void sendMessage(String... messages) {
                if (sender.isLog())
                    finalS.sendMessage(messages);
            }

            @Override
            public void sendMessage(UUID s1, String message) {
                if (sender.isLog())
                    finalS.sendMessage(s1, message);
            }

            @Override
            public void sendMessage(UUID s1, String... messages) {
                if (sender.isLog())
                    finalS.sendMessage(s1, messages);
            }

            @Override
            public Server getServer() {
                return finalS.getServer();
            }

            @Override
            public String getName() {
                return finalS.getName();
            }

            @Override
            public Spigot spigot() {
                return finalS.spigot();
            }

            @Override
            public boolean isPermissionSet(String name) {
                return finalS.isPermissionSet(name);
            }

            @Override
            public boolean isPermissionSet(Permission perm) {
                return finalS.isPermissionSet(perm);
            }

            @Override
            public boolean hasPermission(String name) {
                return finalS.hasPermission(name);
            }

            @Override
            public boolean hasPermission(Permission perm) {
                return finalS.hasPermission(perm);
            }

            @Override
            public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
                return finalS.addAttachment(plugin, name, value);
            }

            @Override
            public PermissionAttachment addAttachment(Plugin plugin) {
                return finalS.addAttachment(plugin);
            }

            @Override
            public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
                return finalS.addAttachment(plugin, name, value, ticks);
            }

            @Override
            public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
                return finalS.addAttachment(plugin, ticks);
            }

            @Override
            public void removeAttachment(PermissionAttachment attachment) {
                finalS.removeAttachment(attachment);
            }

            @Override
            public void recalculatePermissions() {
                finalS.recalculatePermissions();
            }

            @Override
            public Set<PermissionAttachmentInfo> getEffectivePermissions() {
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
