package net.blumasc.excitingenchants.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.blumasc.blubasics.item.client.InWorld3dBakedModel;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.block.entity.renderer.TrapClientExtension;
import net.blumasc.excitingenchants.client.*;
import net.blumasc.excitingenchants.effect.ModEffects;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.entity.client.castle.CastleRenderLayer;
import net.blumasc.excitingenchants.entity.client.souls.SoulRetrieverRenderLayer;
import net.blumasc.excitingenchants.item.ModItems;
import net.blumasc.excitingenchants.network.HorrorRequestPacket;
import net.blumasc.excitingenchants.network.HorrorSavePacket;
import net.blumasc.excitingenchants.network.InventoryOpenPayload;
import net.blumasc.excitingenchants.network.JumpInputPacket;
import net.blumasc.excitingenchants.shader.OilSlickRenderer;
import net.minecraft.client.Camera;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@EventBusSubscriber(modid = ExcitingEnchantsMod.MODID, value = Dist.CLIENT)
public class ClientHimHandler {
    private static final int SYNC_THRESHOLD = 10;
    private static int lastSyncedValue = 0;
    private static int lastPhase = -1;

    @SubscribeEvent
    public static void onClientJoin(ClientPlayerNetworkEvent.LoggingIn event) {
        ClientHimData.horror_status = 0;
        lastSyncedValue = 0;
        lastPhase = -1;
        PacketDistributor.sendToServer(new HorrorRequestPacket());
    }

    @SubscribeEvent
    public static void onClientLeave(ClientPlayerNetworkEvent.LoggingOut event) {
        if (Math.abs(ClientHimData.horror_status - lastSyncedValue) > 0) {
            PacketDistributor.sendToServer(new HorrorSavePacket(ClientHimData.horror_status));
        }
        ClientHimData.horror_status = 0;
        lastSyncedValue = 0;
        lastPhase = -1;
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) return;

        long dayTime = mc.level.getDayTime() % 24000;
        int currentPhase = (dayTime < 6000 || dayTime >= 18000) ? 0 : 1;

        if (currentPhase != lastPhase && lastPhase != -1) {
            handleTimeInterval(mc.player);
        }

        lastPhase = currentPhase;
    }

    private static void handleTimeInterval(LocalPlayer player) {
        int armorPieces = countHorrorArmorPieces(player);

        if (armorPieces > 0) {
            ClientHimData.horror_status += armorPieces;
            ClientHimData.horror_status = Math.min(100, ClientHimData.horror_status);
        } else {
            int reduction = (int) Math.ceil(ClientHimData.horror_status / 4.0);
            ClientHimData.horror_status = Math.max(0, ClientHimData.horror_status - reduction);
        }

        if (Math.abs(ClientHimData.horror_status - lastSyncedValue) >= SYNC_THRESHOLD) {
            PacketDistributor.sendToServer(new HorrorSavePacket(ClientHimData.horror_status));
            lastSyncedValue = ClientHimData.horror_status;
        }
    }

    private static int countHorrorArmorPieces(LocalPlayer player) {
        int count = 0;
        for (ItemStack stack : player.getArmorSlots()) {
            if (!stack.isEmpty() && hasHorrorEnchantment(stack)) {
                count++;
            }
        }
        return count;
    }

    private static boolean hasHorrorEnchantment(ItemStack stack) {
        return stack.getEnchantments().keySet().stream()
                .anyMatch(holder -> holder.is(
                        ModEnchantments.HIM));
    }

    public static void setLastSyncedValue(int value) {
        lastSyncedValue = value;
    }

    private static final ResourceLocation HIM_SKIN =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/him_skin.png");

    private static final Random RANDOM = new Random();
    private static float himSpawnChance = 0f;
    private static long lastHimSpawnTick = 0;
    private static final int HIM_SPAWN_COOLDOWN = 200;

    @SubscribeEvent
    public static void onFogDensity(ViewportEvent.RenderFog event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !isWearingHorrorArmor(mc.player)) return;

        int horror = ClientHimData.horror_status;
        if (horror < 25) return;

        float t = Mth.clamp((horror - 25f) / 75f, 0f, 1f);

        float defaultFar = event.getFarPlaneDistance();

        event.setNearPlaneDistance(Mth.lerp(t, defaultFar * 0.6f, 2f));
        event.setFarPlaneDistance(Mth.lerp(t, defaultFar, 8f));
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBlockRender(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !isWearingHorrorArmor(mc.player)) return;
        if (ClientHimData.horror_status < 75) return;

        HorrorBlockRenderHandler.torchSwapActive = true;
    }

    @SubscribeEvent
    public static void onRenderLevelLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !isWearingHorrorArmor(mc.player)) return;
        if (ClientHimData.horror_status < 50) return;

        long currentTick = mc.level.getGameTime();
        if (currentTick - lastHimSpawnTick < HIM_SPAWN_COOLDOWN) return;
        himSpawnChance = (ClientHimData.horror_status - 50f) / 50f;
        if (RANDOM.nextFloat() > himSpawnChance) return;

        lastHimSpawnTick = currentTick;

        renderHimAtFogEdge(event.getPoseStack(), event.getProjectionMatrix(), mc);
    }

    private static void renderHimAtFogEdge(PoseStack poseStack, Matrix4f projectionMatrix, Minecraft mc) {
        LocalPlayer player = mc.player;
        float fogEdgeDistance = getFogEdgeDistance();

        float angle = RANDOM.nextFloat() * 360f;
        double radians = Math.toRadians(angle);

        double dx = Math.sin(radians) * fogEdgeDistance;
        double dz = Math.cos(radians) * fogEdgeDistance;

        double x = player.getX() + dx;
        double y = player.getY();
        double z = player.getZ() + dz;

        Camera camera = mc.gameRenderer.getMainCamera();
        Vec3 camPos = camera.getPosition();

        poseStack.pushPose();
        poseStack.translate(x - camPos.x, y - camPos.y, z - camPos.z);

        poseStack.mulPose(Axis.YP.rotationDegrees(-angle + 180f));

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShaderTexture(0, HIM_SKIN);

        float alpha = Math.min(1f, himSpawnChance);
        renderHimBillboard(poseStack, mc.renderBuffers().bufferSource(), alpha);

        RenderSystem.disableBlend();
        poseStack.popPose();
    }

    private static void renderHimBillboard(PoseStack poseStack, MultiBufferSource bufferSource, float alpha) {
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityTranslucent(HIM_SKIN));
        PoseStack.Pose pose = poseStack.last();

        float w = 0.5f;
        float h = 2f;

        for (int side = 0; side < 2; side++) {
            float xSign = side == 0 ? 1f : -1f;
            consumer.addVertex(pose, -w * xSign, 0,  0).setColor(1f, 1f, 1f, alpha).setUv(side == 0 ? 0f : 1f, 1f).setNormal(pose, 0, 1, 0);
            consumer.addVertex(pose,  w * xSign, 0,  0).setColor(1f, 1f, 1f, alpha).setUv(side == 0 ? 1f : 0f, 1f).setNormal(pose, 0, 1, 0);
            consumer.addVertex(pose,  w * xSign, h,  0).setColor(1f, 1f, 1f, alpha).setUv(side == 0 ? 1f : 0f, 0f).setNormal(pose, 0, 1, 0);
            consumer.addVertex(pose, -w * xSign, h,  0).setColor(1f, 1f, 1f, alpha).setUv(side == 0 ? 0f : 1f, 0f).setNormal(pose, 0, 1, 0);
        }
    }

    private static float getFogEdgeDistance() {
        int horror = ClientHimData.horror_status;
        float t = Math.clamp((horror - 25f) / 75f, 0f, 1f);
        return Mth.lerp(t, 48f, 8f);
    }

    private static boolean isWearingHorrorArmor(LocalPlayer player) {
        for (ItemStack stack : player.getArmorSlots()) {
            if (!stack.isEmpty() && hasHorrorEnchantment(stack)) return true;
        }
        return false;
    }
}
