package net.blumasc.excitingenchants.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.world.item.ItemStack;

public class MagneticItemParticle extends BreakingItemParticle {

    private MagneticItemParticle(ClientLevel level, double x, double y, double z,
                                 double vx, double vy, double vz, ItemStack stack) {
        super(level, x, y, z, vx, vy, vz, stack);
        this.gravity = 0.0f;
        this.hasPhysics = false;
        this.xd = vx;
        this.yd = vy;
        this.zd = vz;
    }

    public static class Provider implements ParticleProvider<MagneticItemParticleOption> {
        @Override
        public Particle createParticle(MagneticItemParticleOption options, ClientLevel level,
                                       double x, double y, double z,
                                       double vx, double vy, double vz) {
            return new MagneticItemParticle(level, x, y, z, vx, vy, vz, options.getItemStack());
        }
    }
}