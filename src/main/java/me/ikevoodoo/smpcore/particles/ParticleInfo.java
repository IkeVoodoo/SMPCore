package me.ikevoodoo.smpcore.particles;

import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;

public record ParticleInfo(Particle type, Location location, Color color, int amount, float size) {



}
