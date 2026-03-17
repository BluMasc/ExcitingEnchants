package net.blumasc.excitingenchants;

import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.block.entity.ModBlockEntities;
import net.blumasc.excitingenchants.block.entity.renderer.CurseTableBlockEntityRenderer;
import net.blumasc.excitingenchants.block.entity.renderer.ReverberatingBlockEntityRenderer;
import net.blumasc.excitingenchants.block.entity.renderer.TrapBlockEntityRenderer;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.entity.ModEntities;
import net.blumasc.excitingenchants.entity.client.*;
import net.blumasc.excitingenchants.entity.client.balloon.BalloonRenderer;
import net.blumasc.excitingenchants.entity.client.bident.BidentRenderer;
import net.blumasc.excitingenchants.item.ModItems;
import net.blumasc.excitingenchants.particle.MagneticItemParticle;
import net.blumasc.excitingenchants.particle.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterNamedRenderTypesEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import org.jetbrains.annotations.Nullable;

@Mod(value = ExcitingEnchantsMod.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = ExcitingEnchantsMod.MODID, value = Dist.CLIENT)
public class ExcitingEnchantsModClient {
    public ExcitingEnchantsModClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {

        EntityRenderers.register(ModEntities.GRAPE_SHOT.get(), ProjectileEmptyRenderer::new);
        EntityRenderers.register(ModEntities.WARDEN_BEAM.get(), ProjectileEmptyRenderer::new);
        EntityRenderers.register(ModEntities.WOOD_ARROW.get(), WoodArrowRenderer::new);
        EntityRenderers.register(ModEntities.LIGHTNING_ROD_ARROW.get(), LightningRodArrowRenderer::new);
        EntityRenderers.register(ModEntities.BLOOD_SHARD.get(), BloodShardRenderer::new);
        EntityRenderers.register(ModEntities.BALLOON.get(), BalloonRenderer::new);
        EntityRenderers.register(ModEntities.BIDENT.get(), BidentRenderer::new);
        EntityRenderers.register(ModEntities.NERF_ARROW.get(), NerfArrowRenderer::new);
        EntityRenderers.register(ModEntities.REDSTONE_ARC.get(), RedstoneArcRenderer::new);

        ItemColors itemColors = Minecraft.getInstance().getItemColors();

        itemColors.register((stack, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }

            Holder<Enchantment> devilsTool = getDevilsToolHolder();
            if (devilsTool == null) {
                return -1;
            }

            ItemEnchantments enchants = stack.get(DataComponents.ENCHANTMENTS);
            if (enchants != null && enchants.getLevel(devilsTool) > 0) {
                return 0xFFF04040;
            }

            return -1;
        }, Items.TRIDENT);

        itemColors.register((stack, tintIndex) -> {
            if (tintIndex != 0) {
                return -1;
            }

            Holder<Enchantment> devilsTool = getDevilsToolHolder();
            if (devilsTool == null) {
                return -1;
            }

            ItemEnchantments enchants = stack.get(DataComponents.ENCHANTMENTS);
            if (enchants != null && enchants.getLevel(devilsTool) > 0) {
                return 0xFFF04040;
            }

            return -1;
        }, ModItems.BIDENT);
    }

    @Nullable
    private static Holder<Enchantment> getDevilsToolHolder() {
        var mc = Minecraft.getInstance();

        if (mc.getConnection() == null) return null;

        return mc.getConnection()
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .get(ModEnchantments.DEVILS_TOOL)
                .orElse(null);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event){
        event.registerBlockEntityRenderer(ModBlockEntities.PITFALL_TRAP_BE.get(), TrapBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.CURSE_TABLE_BE.get(), CurseTableBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(ModBlockEntities.BREAKING_BLOCK_BE.get(), ReverberatingBlockEntityRenderer::new);
    }
    @SubscribeEvent
    public static void onRegisterParticles(RegisterParticleProvidersEvent event) {
        event.registerSpecial(ModParticles.MAGNETIC_ITEM.get(), new MagneticItemParticle.Provider());
    }
}
