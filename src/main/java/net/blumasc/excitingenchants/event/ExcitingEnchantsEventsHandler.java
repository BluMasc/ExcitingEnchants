package net.blumasc.excitingenchants.event;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.entity.renderer.HornModel;
import net.blumasc.excitingenchants.entity.ModEntities;
import net.blumasc.excitingenchants.entity.client.balloon.BalloonModel;
import net.blumasc.excitingenchants.entity.client.bident.BidentModel;
import net.blumasc.excitingenchants.entity.client.castle.CastleModel;
import net.blumasc.excitingenchants.entity.custom.BidentEntity;
import net.blumasc.excitingenchants.item.ModItems;
import net.blumasc.excitingenchants.item.custom.BidentItem;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.level.Level;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.common.data.fixes.NeoForgeEntityLegacyAttributesFix;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = ExcitingEnchantsMod.MODID)
public class ExcitingEnchantsEventsHandler {
    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(HornModel.LAYER_LOCATION, HornModel::createBodyLayer);
        event.registerLayerDefinition(CastleModel.LAYER_LOCATION, CastleModel::createBodyLayer);
        event.registerLayerDefinition(BalloonModel.LAYER_LOCATION, BalloonModel::createBodyLayer);
        event.registerLayerDefinition(BidentModel.LAYER_LOCATION, BidentModel::createBodyLayer);
    }
    @SubscribeEvent
    public static void registerAtributes(EntityAttributeCreationEvent event) {
        event.put(ModEntities.BALLOON.get(), LivingEntity.createLivingAttributes().add(Attributes.MAX_HEALTH, 1d)
                .add(Attributes.MOVEMENT_SPEED, 0.2f)
                .add(Attributes.FOLLOW_RANGE, 24D)
                .add(Attributes.GRAVITY, 0)
                .add(Attributes.STEP_HEIGHT, 0)
                .add(Attributes.MOVEMENT_EFFICIENCY, 0)
                .add(Attributes.SCALE, 1).build());
    }
    @SubscribeEvent
    public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        InteractionHand hand = event.getHand();
        if (hand != InteractionHand.MAIN_HAND) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        if (!(mainHand.getItem() instanceof ShearsItem)) return;
        if (!offHand.is(ModItems.OVERGROWN_SPEAR.get())) return;

        Level level = player.level();
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS);

        if (!level.isClientSide()) {
            ItemStack kelp = new ItemStack(Items.KELP, player.getRandom().nextInt(3) + 1);
            if (!player.addItem(kelp)) {
                player.spawnAtLocation(kelp);
            }

            mainHand.hurtAndBreak(1, player, LivingEntity.getSlotForHand(InteractionHand.MAIN_HAND));
            player.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(ModItems.RUSTED_SPEAR.get()));
        }

        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!left.is(ModItems.MELLEABLE_SPEAR.get())) return;
        if (!right.is(Items.IRON_INGOT)) return;
        if (right.getCount() < 3) return;

        ItemStack result = new ItemStack(ModItems.REPAIRED_SPEAR.get());
        event.setOutput(result);
        event.setCost(3);
        event.setMaterialCost(3);
    }

    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        if (event.getEntity().getClass() != ThrownTrident.class) return;
        if (event.getLevel().isClientSide()) return;

        ThrownTrident vanilla = (ThrownTrident) event.getEntity();
        ItemStack stack = vanilla.pickupItemStack;
        if (!(stack.getItem() instanceof BidentItem)) return;

        event.setCanceled(true);

        BidentEntity bident = new BidentEntity(ModEntities.BIDENT.get(), event.getLevel());

        bident.setPos(vanilla.getX(), vanilla.getY(), vanilla.getZ());
        bident.setDeltaMovement(vanilla.getDeltaMovement());
        bident.setXRot(vanilla.getXRot());
        bident.setYRot(vanilla.getYRot());
        bident.setOwner(vanilla.getOwner());


        bident.pickupItemStack = stack.copy();

        event.getLevel().addFreshEntity(bident);
    }
}
