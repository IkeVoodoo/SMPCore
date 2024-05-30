package me.ikevoodoo.smpcore.handlers.elimination;

import me.ikevoodoo.smpcore.handlers.EliminationData;
import org.bukkit.entity.Player;

public interface AdvancedEliminationHandler {

    boolean eliminatedPlayerJoined(Player player, EliminationData data);

    void playerEliminated(Player player, EliminationData data);

    void playerRevived(Player player, EliminationData data);

}
