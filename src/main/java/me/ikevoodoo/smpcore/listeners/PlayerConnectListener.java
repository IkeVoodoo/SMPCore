package me.ikevoodoo.smpcore.listeners;

import me.ikevoodoo.smpcore.SMPPlugin;
import me.ikevoodoo.smpcore.handlers.EliminationHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerConnectListener implements Listener {

    private final SMPPlugin plugin;

    public PlayerConnectListener(SMPPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void on(PlayerJoinEvent e) {
        plugin.getJoinActionHandler().fire(e.getPlayer().getUniqueId());

        EliminationHandler handler = plugin.getEliminationHandler();
        if(handler.isEliminated(e.getPlayer())) {
            long banTime = handler.getBanTime(e.getPlayer());
            if(banTime - System.currentTimeMillis() > 0) {
                e.setJoinMessage("");
                e.getPlayer().kickPlayer("§cYou have been eliminated.");
                return;
            }

            handler.revive(e.getPlayer());
        }
    }

}
