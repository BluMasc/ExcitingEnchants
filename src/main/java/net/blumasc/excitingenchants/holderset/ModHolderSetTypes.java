package net.blumasc.excitingenchants.holderset;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.holdersets.HolderSetType;
import net.neoforged.neoforge.registries.holdersets.ICustomHolderSet;

public class ModHolderSetTypes {
    public static final DeferredRegister<HolderSetType> HOLDER_SET_TYPES =
        DeferredRegister.create(NeoForgeRegistries.HOLDER_SET_TYPES, ExcitingEnchantsMod.MODID);

    public static final DeferredHolder<HolderSetType, HolderSetType> ALL_BLOCK_ITEMS =
            HOLDER_SET_TYPES.register("all_block_items", () -> new HolderSetType() {
                @Override
                public <T> MapCodec<? extends ICustomHolderSet<T>> makeCodec(ResourceKey<? extends Registry<T>> key, Codec<Holder<T>> holderCodec, boolean b) {
                    return (MapCodec) AllBlockItemsHolderSet.CODEC;
                }

                @Override
                public <T> StreamCodec<RegistryFriendlyByteBuf, ? extends ICustomHolderSet<T>> makeStreamCodec(ResourceKey<? extends Registry<T>> key) {
                    return (StreamCodec) StreamCodec.unit(new AllBlockItemsHolderSet());
                }
            });

    public static final DeferredHolder<HolderSetType, HolderSetType> ALL_ITEMS =
            HOLDER_SET_TYPES.register("all_items", () -> new HolderSetType() {
                @Override
                public <T> MapCodec<? extends ICustomHolderSet<T>> makeCodec(ResourceKey<? extends Registry<T>> key, Codec<Holder<T>> holderCodec, boolean b) {
                    return (MapCodec) EverythingItemsHolderSet.CODEC;
                }

                @Override
                public <T> StreamCodec<RegistryFriendlyByteBuf, ? extends ICustomHolderSet<T>> makeStreamCodec(ResourceKey<? extends Registry<T>> key) {
                    return (StreamCodec) StreamCodec.unit(new EverythingItemsHolderSet());
                }
            });

    public static void register(IEventBus e){
        HOLDER_SET_TYPES.register(e);
    }
}