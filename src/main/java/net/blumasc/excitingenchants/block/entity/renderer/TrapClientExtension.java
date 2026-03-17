package net.blumasc.excitingenchants.block.entity.renderer;

import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import net.neoforged.neoforge.client.extensions.common.IClientBlockExtensions;

public class TrapClientExtension implements IClientBlockExtensions {

    @Override
    public boolean addDestroyEffects(
            BlockState state,
            Level level,
            BlockPos pos,
            ParticleEngine engine
    ) {
        return true;
    }

    @Override
    public boolean addHitEffects(
            BlockState state,
            Level level,
            HitResult hit,
            ParticleEngine engine
    ) {
        return true;
    }
}
