package me.ikevoodoo.smpcore.commands.functional;

import me.ikevoodoo.smpcore.SMPPlugin;

public interface CommandCreator {

    default FunctionalCommand createCommand(SMPPlugin plugin) {
        return new FunctionalCommand(plugin);
    }

}
