package net.blumasc.excitingenchants;

import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.block.entity.ModBlockEntities;
import net.blumasc.excitingenchants.effect.ModEffects;
import net.blumasc.excitingenchants.enchantment.ModEnchantmentEffects;
import net.blumasc.excitingenchants.entity.ModEntities;
import net.blumasc.excitingenchants.holderset.ModHolderSetTypes;
import net.blumasc.excitingenchants.item.ModCreativeModeTabs;
import net.blumasc.excitingenchants.item.ModItems;
import net.blumasc.excitingenchants.lootmodifiers.ModLootModifiers;
import net.blumasc.excitingenchants.network.ModNetworking;
import net.blumasc.excitingenchants.particle.ModParticles;
import net.blumasc.excitingenchants.sound.ModSounds;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

@Mod(ExcitingEnchantsMod.MODID)
public class ExcitingEnchantsMod {
    public static final String MODID = "excitingenchants";
    public ExcitingEnchantsMod(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);

        ModCreativeModeTabs.register(modEventBus);

        NeoForge.EVENT_BUS.register(this);

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModSounds.register(modEventBus);
        ModEntities.register(modEventBus);
        ModEffects.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEnchantmentEffects.register(modEventBus);
        modEventBus.addListener(ModNetworking::register);
        ModHolderSetTypes.register(modEventBus);
        ModParticles.PARTICLE_TYPES.register(modEventBus);
        ModLootModifiers.register(modEventBus);

        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }
}
