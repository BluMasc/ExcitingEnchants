package net.blumasc.excitingenchants.particle;

import com.mojang.serialization.MapCodec;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(Registries.PARTICLE_TYPE, ExcitingEnchantsMod.MODID);

    public static final DeferredHolder<ParticleType<?>, ParticleType<MagneticItemParticleOption>> MAGNETIC_ITEM =
            PARTICLE_TYPES.register("magnetic_item", () -> new ParticleType<>(false) {
                @Override
                public MapCodec<MagneticItemParticleOption> codec() {
                    return MagneticItemParticleOption.CODEC;
                }

                @Override
                public StreamCodec<? super RegistryFriendlyByteBuf, MagneticItemParticleOption> streamCodec() {
                    return MagneticItemParticleOption.STREAM_CODEC;
                }
            });
}