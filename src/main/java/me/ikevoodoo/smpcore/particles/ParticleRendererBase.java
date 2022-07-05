package me.ikevoodoo.smpcore.particles;

import org.bukkit.World;
import org.bukkit.entity.Player;

public interface ParticleRendererBase {

    void render(World world);

    void render(World world, Player player);

}
