package me.ikevoodoo.smpcore.callbacks.eliminations;

import me.ikevoodoo.smpcore.callbacks.Callback;
import org.bukkit.entity.Player;

public interface EliminationCallback extends Callback {

    void whenTriggered(EliminationType type, Player player);

}
