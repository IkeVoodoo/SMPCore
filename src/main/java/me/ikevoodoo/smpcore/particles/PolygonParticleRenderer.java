package me.ikevoodoo.smpcore.particles;

import me.ikevoodoo.smpcore.math.helper.LineHelper;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PolygonParticleRenderer implements ParticleRendererBase {

    private final List<ParticleInfo> particles = new ArrayList<>();
    private boolean fill;
    private boolean edges;
    private double spacing = 2;

    public PolygonParticleRenderer fill(boolean fill) {
        this.fill = fill;
        return this;
    }

    public PolygonParticleRenderer edges(boolean edges) {
        this.edges = edges;
        return this;
    }

    public PolygonParticleRenderer spacing(double spacing) {
        this.spacing = spacing;
        return this;
    }

    public PolygonParticleRenderer particle(ParticleInfo particle) {
        this.particles.add(particle);
        return this;
    }

    @Override
    public void render(World world) {
        if (this.fill) {

            return;
        }

        if (this.edges) {
            ParticleInfo prev = null;
            for (ParticleInfo particle : this.particles) {
                Consumer<Vector> consumer;
                if (particle.type() == Particle.REDSTONE) {
                    consumer = pos ->
                            world.spawnParticle(Particle.REDSTONE, pos.toLocation(world), particle.amount(), new Particle.DustOptions(particle.color(), particle.size()));
                } else {
                    consumer = pos ->
                            world.spawnParticle(particle.type(), pos.toLocation(world), particle.amount());
                }

                if (prev != null)
                    LineHelper.runOnLine(prev.location().toVector(), particle.location().toVector(), this.spacing, consumer);
                else
                    consumer.accept(particle.location().toVector());
                prev = particle;
            }
            return;
        }

        for (ParticleInfo particle : this.particles) {
            if (particle == null) continue;
            for (ParticleInfo particleInfo : this.particles) {
                if (particleInfo == null || particleInfo.equals(particle)) continue;
                Consumer<Vector> consumer;
                if (particle.type() == Particle.REDSTONE) {
                    consumer = pos ->
                            world.spawnParticle(Particle.REDSTONE, pos.toLocation(world), particle.amount(), new Particle.DustOptions(particle.color(), particle.size()));
                } else {
                    consumer = pos ->
                            world.spawnParticle(particle.type(), pos.toLocation(world), particle.amount());
                }

                LineHelper.runOnLine(particleInfo.location().toVector(), particle.location().toVector(), this.spacing, consumer);
            }

        }
    }

    @Override
    public void render(World world, Player player) {
        if (this.fill) {

            return;
        }
    }

    private void sendParticles(Location start, Location end, Function<ParticleInfo, Consumer<Location>> spawnerCreator) {
        if (this.edges) {
            ParticleInfo previous = null;

            for (var info : this.particles) {
                var spawner = spawnerCreator.apply(info);

                if (previous != null) {
                    LineHelper.runOnLine(start, end, this.spacing, spawner);
                    continue;
                }

                spawner.accept(info.location());
            }

            return;
        }

        for (var info : this.particles) {
            if (info == null) continue;

            var spawner = spawnerCreator.apply(info);

            for (var otherInfo : this.particles) {
                if (otherInfo == null || otherInfo == info) continue;

                LineHelper.runOnLine(info.location(), otherInfo.location(), this.spacing, spawner);
            }
        }
    }
}
