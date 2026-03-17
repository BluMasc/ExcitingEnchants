package net.blumasc.excitingenchants.enchantment;

import com.mojang.serialization.MapCodec;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModEnchantmentEffects {
    public static final DeferredRegister<MapCodec<? extends EnchantmentValueEffect>> ENTITY_ENCHANTMENT_EFFECTS =
            DeferredRegister.create(Registries.ENCHANTMENT_VALUE_EFFECT_TYPE, ExcitingEnchantsMod.MODID);


    public static void register(IEventBus bus){
       ENTITY_ENCHANTMENT_EFFECTS.register(bus);
   }
}
