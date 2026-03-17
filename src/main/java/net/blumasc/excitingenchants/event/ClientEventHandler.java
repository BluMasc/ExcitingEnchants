package net.blumasc.excitingenchants.event;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blumasc.blubasics.item.client.InWorld3dBakedModel;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.block.entity.renderer.TrapClientExtension;
import net.blumasc.excitingenchants.client.ClientDiviningData;
import net.blumasc.excitingenchants.client.ClientInkedData;
import net.blumasc.excitingenchants.client.ClientRageData;
import net.blumasc.excitingenchants.client.ClientSoulData;
import net.blumasc.excitingenchants.effect.ModEffects;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.entity.client.castle.CastleRenderLayer;
import net.blumasc.excitingenchants.entity.client.souls.SoulRetrieverRenderLayer;
import net.blumasc.excitingenchants.item.ModItems;
import net.blumasc.excitingenchants.network.InventoryOpenPayload;
import net.blumasc.excitingenchants.network.JumpInputPacket;
import net.blumasc.excitingenchants.shader.OilSlickRenderer;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.core.registries.Registries;
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

import java.util.*;

@EventBusSubscriber(modid = ExcitingEnchantsMod.MODID, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerBlock(
                new TrapClientExtension(),
                ModBlocks.PITFALL_TRAP.get()
        );
    }

    @SubscribeEvent
    public static void onPlaySound(PlaySoundEvent event) {
        if (event.getSound() == null) return;
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if(player != null) {
            Holder<Enchantment> hungryHolder = player.level().registryAccess()
                    .registryOrThrow(Registries.ENCHANTMENT)
                    .getHolderOrThrow(ModEnchantments.SCULKING);

            ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
            int level = helmet.getEnchantmentLevel(hungryHolder);
            if (level>0) {
                double dx = player.getX() - event.getSound().getX();
                double dy = player.getY() - event.getSound().getY();
                double dz = player.getZ() - event.getSound().getZ();
                double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);

                double maxDistance = 5.0 +(5.0 * level);
                if (distance <= maxDistance && distance>=0.1) {
                    spawnSculkParticles(mc.player, event.getSound().getX(), event.getSound().getY(), event.getSound().getZ());
                }
            }
        }
    }

    private static void spawnSculkParticles(LocalPlayer player, double x, double y, double z) {
        ClientLevel world = (ClientLevel) player.level();

        VibrationParticleOption vpo = new VibrationParticleOption(new PositionSource() {
            @Override
            public Optional<Vec3> getPosition(Level level) {
                return Optional.of(player.position().add(0,player.getEyeHeight()-0.1,0));
            }

            @Override
            public PositionSourceType<? extends PositionSource> getType() {
                return PositionSourceType.ENTITY;
            }
        }, 15);

        for (int i = 0; i < 20; i++) {
            world.addParticle(
                    vpo,
                    x,
                    y,
                    z,
                    0.1,
                    0.15,
                    0.1
            );
        }
    }

    private static final Minecraft mc = Minecraft.getInstance();
    private static final ResourceLocation ARROW_TEXTURE = ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/gui/arrow.png");
    private static final int ARROW_SIZE = 5;
    private static final float DISTANCE_FROM_CROSSHAIR = 10f;

    @SubscribeEvent
    public static void onRenderDiviningGui(RenderGuiLayerEvent.Post event) {
        LocalPlayer player = mc.player;
        if (player == null) return;

        GuiGraphics gui = event.getGuiGraphics();

        int centerX = mc.getWindow().getGuiScaledWidth() / 2;
        int centerY = mc.getWindow().getGuiScaledHeight() / 2;

        Vec3 playerPos = player.position();

        for (Vec3 mobPos : ClientDiviningData.EnemyPositions) {
            renderArrow(gui, centerX, centerY, playerPos, mobPos, 0xFFFF0000);
        }

        for (Vec3 otherPlayerPos : ClientDiviningData.PlayerPositions) {
            renderArrow(gui, centerX, centerY, playerPos, otherPlayerPos, 0xFF0000FF);
        }
    }

    private static void renderArrow(GuiGraphics gui, int centerX, int centerY, Vec3 playerPos, Vec3 targetPos, int color) {
        double dx = targetPos.x - playerPos.x;
        double dz = targetPos.z - playerPos.z;

        double angle = Math.toDegrees(Math.atan2(dz, dx)) - mc.player.getYRot();

        double rad = Math.toRadians(angle);

        int x = (int) (centerX - Math.cos(rad) * DISTANCE_FROM_CROSSHAIR);
        int y = (int) (centerY - Math.sin(rad) * DISTANCE_FROM_CROSSHAIR);

        gui.pose().pushPose();
        gui.pose().translate(x, y, 200);
        gui.pose().mulPose(com.mojang.math.Axis.ZP.rotationDegrees((float) angle-90));

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        RenderSystem.setShaderColor(r, g, b, a);

        gui.blit(ARROW_TEXTURE, -ARROW_SIZE / 2, -ARROW_SIZE / 2, ARROW_SIZE, ARROW_SIZE,
                0, 0, ARROW_SIZE, ARROW_SIZE, ARROW_SIZE, ARROW_SIZE);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        gui.pose().popPose();
    }

    private static final ResourceLocation INK_OVERLAY =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/gui/ink/ink_overlay.png");

    private static final ResourceLocation INK_SPLOOCH_0 =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/gui/ink/ink_sploch_0.png");

    private static final ResourceLocation INK_SPLOOCH_1 =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/gui/ink/ink_sploch_1.png");

    private static final ResourceLocation INK_SPLOOCH_2 =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/gui/ink/ink_sploch_2.png");

    private static final ResourceLocation[] SPLOOCH_TEXTURES = {
            INK_SPLOOCH_0, INK_SPLOOCH_1, INK_SPLOOCH_2
    };


    private static final int[][] SPLOOCH_TEX_DIMS = {
            {1930, 1817},
            {1920, 1805},
            {650, 1280}
    };

    private static int lastInkLevel = -1;
    private static final Random RANDOM = new Random();

    @SubscribeEvent
    public static void renderInk(RenderGuiEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        int w = event.getGuiGraphics().guiWidth();
        int h = event.getGuiGraphics().guiHeight();

        MobEffectInstance effect = mc.player.getEffect(ModEffects.INKED);

        if (effect == null) {
            if (lastInkLevel != -1) {
                ClientInkedData.inkSploches = null;
                lastInkLevel = -1;
            }
            return;
        }

        int currentLevel = effect.getAmplifier() + 1;
        if (currentLevel != lastInkLevel || ClientInkedData.inkSploches == null) {
            lastInkLevel = currentLevel;
            ClientInkedData.inkSploches = new ArrayList<>();

            for (int i = 0; i < currentLevel+2; i++) {
                int type = RANDOM.nextInt(3);
                int[] texDims = SPLOOCH_TEX_DIMS[type];

                int renderH, renderW;
                if (type == 2) {
                    renderH = h;
                    renderW = (int)(h * (texDims[0] / (float) texDims[1]));
                } else {
                    renderH = (int)(h / 1.3);
                    renderW = (int)(renderH * (texDims[0] / (float) texDims[1]));
                }

                int x = RANDOM.nextInt(w + renderW) - renderW / 2;
                int y = type == 2 ? 0 : RANDOM.nextInt(h + renderH) - renderH / 2;

                float rotation;
                if (type == 2) {
                    int[] increments = {0, 90, 180, 270};
                    rotation = increments[RANDOM.nextInt(4)];
                } else {
                    rotation = RANDOM.nextFloat() * 360f;
                }
                boolean mirrored = RANDOM.nextBoolean();
                int finalRenderW = renderW;
                int finalRenderH = renderH;
                if (type == 2 && (rotation == 90f || rotation == 270f)) {
                    finalRenderW = w;
                    finalRenderH = (int)(w * (texDims[0] / (float) texDims[1]));
                }

                ClientInkedData.inkSploches.add(
                        new ClientInkedData.InkSplooch(x, y, type, finalRenderW, finalRenderH, rotation, mirrored)
                );
            }
        }

        GuiGraphics gui = event.getGuiGraphics();

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        float alpha = 0.9f;
        int duration = effect.getDuration();
        int fadeTicks = 10 * 20;

        if (duration <= fadeTicks) {
            alpha = 0.9f * (duration / (float) fadeTicks);
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, alpha);

        gui.blit(INK_OVERLAY, 0, 0, w, h, 0, 0, 1280, 1048, 1280, 1048);

        if (ClientInkedData.inkSploches != null) {
            for (ClientInkedData.InkSplooch splooch : ClientInkedData.inkSploches) {
                int[] texDims = SPLOOCH_TEX_DIMS[splooch.type()];

                PoseStack pose = gui.pose();
                pose.pushPose();

                float centerX = splooch.x() + splooch.renderW() / 2.0f;
                float centerY = splooch.y() + splooch.renderH() / 2.0f;
                pose.translate(centerX, centerY, 0);

                pose.mulPose(Axis.ZP.rotationDegrees(splooch.rotation()));

                if (splooch.mirrored()) {
                    pose.scale(-1.0f, 1.0f, 1.0f);
                }

                pose.translate(-splooch.renderW() / 2.0f, -splooch.renderH() / 2.0f, 0);

                gui.blit(SPLOOCH_TEXTURES[splooch.type()],
                        0, 0,
                        splooch.renderW(), splooch.renderH(),
                        0, 0,
                        texDims[0], texDims[1],
                        texDims[0], texDims[1]
                );

                pose.popPose();
            }
        }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (mc.options.getCameraType() != CameraType.FIRST_PERSON) return;

        Holder<Enchantment> soulRetrieverHolder = mc.player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.SOUL_RETRIEVER);

        ItemStack weapon = mc.player.getMainHandItem();
        int level = weapon.getEnchantmentLevel(soulRetrieverHolder);
        if (level <= 0) return;

        List<ClientSoulData.SoulOrbit> souls =
                ClientSoulData.playerSouls.get(mc.player.getUUID());
        if (souls == null || souls.isEmpty()) return;

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource bufferSource = mc.renderBuffers().bufferSource();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

        Vec3 cam = event.getCamera().getPosition();
        float ageInTicks = mc.player.tickCount + event.getPartialTick().getGameTimeDeltaPartialTick(true);

        for (int i = 0; i < souls.size(); i++) {
            ClientSoulData.SoulOrbit soul = souls.get(i);

            float baseAngle = (float)(i * (2 * Math.PI / souls.size()));
            float angle = baseAngle + (ageInTicks * 0.05f);
            float radius = 0.8f;

            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(true);

            double playerX = Mth.lerp(partialTick, mc.player.xOld, mc.player.getX());
            double playerY = Mth.lerp(partialTick, mc.player.yOld, mc.player.getY());
            double playerZ = Mth.lerp(partialTick, mc.player.zOld, mc.player.getZ());

            double worldX = playerX + Math.cos(angle) * radius;
            double worldY = playerY + 1.8 + Math.sin(ageInTicks * 0.03f + i * 2.0f) * 0.15f;
            double worldZ = playerZ + Math.sin(angle) * radius;

            double rx = worldX - cam.x;
            double ry = worldY - cam.y;
            double rz = worldZ - cam.z;

            LivingEntity dummy = SoulRetrieverRenderLayer.getOrCreateDummy(
                    soul.entityType(), mc.player
            );
            if (dummy == null) continue;

            poseStack.pushPose();
            poseStack.translate(rx, ry, rz);
            float yaw = (float) Math.toDegrees(Math.atan2(rx, rz));
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw+180));
            poseStack.scale(0.2f, 0.2f, 0.2f);
            RenderSystem.setShaderColor(0.0f, 0.8f, 0.9f, 1.0f);
            dispatcher.render(
                    dummy, 0, 0, 0, 0,
                    event.getPartialTick().getGameTimeDeltaPartialTick(true),
                    poseStack, bufferSource, 15728880
            );
            ((MultiBufferSource.BufferSource) bufferSource).endBatch();
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);



            poseStack.popPose();
        }
    }

    @SubscribeEvent
    public static void onAddLayers(EntityRenderersEvent.AddLayers event) {
        EntityModelSet models = event.getEntityModels();

        for (PlayerSkin.Model skin : event.getSkins()) {
            PlayerRenderer renderer = event.getSkin(skin);


            if (renderer != null) {
                renderer.addLayer(
                        new CastleRenderLayer<>(renderer, models)
                );
                renderer.addLayer(
                        new SoulRetrieverRenderLayer<>(renderer)
                );
            }


        }
    }

    @SubscribeEvent
    public static void onRegisterItemDecorations(RegisterItemDecorationsEvent event) {
        event.register(Items.CROSSBOW, (guiGraphics, font, stack, itemX, itemY) -> {
            ChargedProjectiles charged = stack.get(DataComponents.CHARGED_PROJECTILES);
            if (charged == null || charged.isEmpty()) return false;

            ResourceLocation overlay = getOverlay(charged.getItems().get(0));
            if (overlay == null) return false;

            guiGraphics.pose().pushPose();
            guiGraphics.pose().translate(0, 0, 300);
            guiGraphics.blit(overlay, itemX, itemY, 0, 0, 16, 16, 16, 16);
            guiGraphics.pose().popPose();

            return false; // return true if you want to cancel the durability bar rendering
        });
    }

    private static ResourceLocation getOverlay(ItemStack projectile) {
        if (projectile.is(Items.ECHO_SHARD))
            return ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/item/overlay/echo_shard.png");
        if (projectile.is(Items.LIGHTNING_ROD))
            return ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/item/overlay/lightning_rod.png");
        if (projectile.is(ModItems.BLOOD_ORB.get()))
            return ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/item/overlay/blood_shard.png");
        if (projectile.is(ItemTags.STONE_TOOL_MATERIALS))
            return ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/item/overlay/pebbles.png");
        return null;
    }

    @SubscribeEvent
    public static void renderRageOverlay(RenderGuiEvent.Pre event) {

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.level == null) return;

        if(ClientRageData.target == null) return;

        if (!(mc.crosshairPickEntity instanceof LivingEntity crosshairEntity)) return;
        if (!crosshairEntity.getUUID().equals(ClientRageData.target)) return;

        GuiGraphics gui = event.getGuiGraphics();
        int w = event.getGuiGraphics().guiWidth();
        int h = event.getGuiGraphics().guiHeight();

        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();

        final int texW = 256;
        final int texH = 256;

        gui.blit( ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/gui/rage/rage_"+ClientRageData.rage+".png"),
                0, 0,
                w, h,
                0, 0,
                texW, texH,
                texW, texH
        );

        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
    }
    @SubscribeEvent
    public static void onRenderRainbow(RenderGuiEvent.Pre event) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        if (mc.player.hasEffect(ModEffects.RAINBOW)) {
            if (!OilSlickRenderer.isLoaded()) {
                OilSlickRenderer.load();
            }
            OilSlickRenderer.apply(event.getPartialTick().getGameTimeDeltaTicks());
            mc.getMainRenderTarget().bindWrite(true);
        } else {
            if (OilSlickRenderer.isLoaded()) {
                OilSlickRenderer.dispose();
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || mc.level == null) return;

        boolean jumpingNow = mc.options.keyJump.isDown();

        if (jumpingNow) {
            if (!player.onGround() && player.getDeltaMovement().y < 0) {
                PacketDistributor.sendToServer(new JumpInputPacket());
            }
        }
    }
    @SubscribeEvent
    public static void onModelSpearBake(ModelEvent.ModifyBakingResult event) {
        ModelResourceLocation inventoryLoc = ModelResourceLocation.inventory(
                ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "swordspear"));
        ModelResourceLocation handLoc = ModelResourceLocation.inventory(
                ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "swordspear_3d"));

        BakedModel inventoryModel = event.getModels().get(inventoryLoc);
        BakedModel handModel = event.getModels().get(handLoc);

        if (inventoryModel != null && handModel != null) {
            event.getModels().put(inventoryLoc, new InWorld3dBakedModel(inventoryModel, handModel));
        }
    }
    @SubscribeEvent
    public static void onModelBidentBake(ModelEvent.ModifyBakingResult event) {
        ModelResourceLocation inventoryLoc = ModelResourceLocation.inventory(
                ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "bident"));
        ModelResourceLocation handLoc = ModelResourceLocation.inventory(
                ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "bident_3d"));

        BakedModel inventoryModel = event.getModels().get(inventoryLoc);
        BakedModel handModel = event.getModels().get(handLoc);

        if (inventoryModel != null && handModel != null) {
            event.getModels().put(inventoryLoc, new InWorld3dBakedModel(inventoryModel, handModel));
        }
    }
    @SubscribeEvent
    public static void onScreenOpen(net.neoforged.neoforge.client.event.ScreenEvent.Opening event) {
        System.out.println(event.getScreen());
        if (event.getScreen() instanceof AbstractContainerScreen) {
            System.out.println("Send Packet Open");
            PacketDistributor.sendToServer(new InventoryOpenPayload(true));
        }
    }

    @SubscribeEvent
    public static void onScreenClose(net.neoforged.neoforge.client.event.ScreenEvent.Closing event) {
        if (event.getScreen() instanceof AbstractContainerScreen) {
            System.out.println("Send Packet Closed");
            PacketDistributor.sendToServer(new InventoryOpenPayload(false));
        }
    }
}
