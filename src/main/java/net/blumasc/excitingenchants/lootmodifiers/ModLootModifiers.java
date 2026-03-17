package net.blumasc.excitingenchants.lootmodifiers;

import com.mojang.serialization.MapCodec;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> LOOT_MODIFIERS =
        DeferredRegister.create(NeoForgeRegistries.GLOBAL_LOOT_MODIFIER_SERIALIZERS, ExcitingEnchantsMod.MODID);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<BadBaitLootModifier>> BAD_BAIT =
        LOOT_MODIFIERS.register("bad_bait", () -> BadBaitLootModifier.CODEC);

    public static final DeferredHolder<MapCodec<? extends IGlobalLootModifier>, MapCodec<MeatDropModifier>> GHOUL_MEAT =
            LOOT_MODIFIERS.register("ghoul_meat", () -> MeatDropModifier.CODEC);

    public static void register(IEventBus eventBus) {
        LOOT_MODIFIERS.register(eventBus);
    }
}