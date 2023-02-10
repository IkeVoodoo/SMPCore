package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.handlers.EliminationHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerConnectListener implements Listener {

    private final SMPPlugin plugin;

    private final List<UUID> removeQuitMessage = new ArrayList<>();

    public PlayerConnectListener(SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        plugin.getJoinActionHandler().fire(e.getPlayer().getUniqueId());
        plugin.getJoinActionHandler().fire(e.getPlayer());


        EliminationHandler handler = plugin.getEliminationHandler();
        if(handler.isEliminated(e.getPlayer())) {
            long banTime = handler.getBanTime(e.getPlayer());
            long bannedAt = handler.getBannedAt(e.getPlayer());
            long current = System.currentTimeMillis();
            if(banTime - (current - bannedAt) > 0) {
                e.setJoinMessage(null);
                removeQuitMessage.add(e.getPlayer().getUniqueId());

                var data = handler.getEliminationData(e.getPlayer());

                e.getPlayer().kickPlayer(data.message());
                handler.markEliminated(e.getPlayer().getUniqueId(), data.withBanTime(banTime));
                return;
            }

            handler.revive(e.getPlayer());
        }
    }

    @EventHandler
    public void on(PlayerQuitEvent e) {
        if (removeQuitMessage.remove(e.getPlayer().getUniqueId())) {
            e.setQuitMessage("");
        }
    }

}
