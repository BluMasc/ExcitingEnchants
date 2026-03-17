package net.blumasc.excitingenchants.particle;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public class MagneticItemParticleOption implements ParticleOptions {
    public static final MapCodec<MagneticItemParticleOption> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(ItemStack.CODEC.fieldOf("item").forGetter(o -> o.itemStack))
                    .apply(instance, MagneticItemParticleOption::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, MagneticItemParticleOption> STREAM_CODEC =
            StreamCodec.composite(
                    ItemStack.STREAM_CODEC,
                    o -> o.itemStack,
                    MagneticItemParticleOption::new);

    private final ItemStack itemStack;

    public MagneticItemParticleOption(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    public ItemStack getItemStack() { return itemStack; }

    @Override
    public ParticleType<?> getType() {
        return ModParticles.MAGNETIC_ITEM.get();
    }
}