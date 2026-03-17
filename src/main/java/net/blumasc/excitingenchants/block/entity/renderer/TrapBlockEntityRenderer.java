package net.blumasc.excitingenchants.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.block.custom.TrapBlock;
import net.blumasc.excitingenchants.block.entity.custom.TrapBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class TrapBlockEntityRenderer implements BlockEntityRenderer<TrapBlockEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/block/trap_layer.png");

    private static final ResourceLocation TEXTURE_COLORABLE =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/block/trap_layer_colorable.png");

    private static final ResourceLocation TEXTURE_INACTIVE =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/block/trap_layer_inactive.png");

    public TrapBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(
            TrapBlockEntity be,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int light,
            int overlay
    ) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;

        if (player == null) return;
        if (!isHoldingShovel(player)) return;

        Level level = be.getLevel();
        BlockState state = null;
        BlockState belowState = null;
        ResourceLocation texture = TEXTURE;
        float r = 1f, g = 1f, b = 1f;

        if (level != null) {
            BlockPos pos = be.getBlockPos();
            state = level.getBlockState(pos);
            BlockPos belowPos = pos.below();
            belowState = level.getBlockState(belowPos);
            if (state.is(ModBlocks.PITFALL_TRAP)) {
                if (state.getValue(TrapBlock.ACTIVE)) {
                    texture = TEXTURE_COLORABLE;
                } else {
                    texture = TEXTURE_INACTIVE;
                }
            }

            try {
                int color = 0xFFFFFF;
                if (belowState != null) {
                    color = belowState.getMapColor(level, belowPos).col;
                }
                r = ((color >> 16) & 0xFF) / 255f;
                g = ((color >> 8) & 0xFF) / 255f;
                b = (color & 0xFF) / 255f;

                if (state.getValue(TrapBlock.ACTIVE) == Boolean.FALSE) {
                    r *= 0.5f;
                    g *= 0.5f;
                    b *= 0.5f;
                }
            } catch (Exception ignored) {
                r = g = b = 1f;
                texture = TEXTURE;
            }
        }

        poseStack.pushPose();
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityTranslucent(texture));
        PoseStack.Pose pose = poseStack.last();
        float y = 0.001f;
        consumer.addVertex(pose, 0.0F, y, 0.0F)
                .setColor((int)(r*255), (int)(g*255), (int)(b*255), 255)
                .setUv(0.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);

        consumer.addVertex(pose, 1.0F, y, 0.0F)
                .setColor((int)(r*255), (int)(g*255), (int)(b*255), 255)
                .setUv(1.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);

        consumer.addVertex(pose, 1.0F, y, 1.0F)
                .setColor((int)(r*255), (int)(g*255), (int)(b*255), 255)
                .setUv(1.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);

        consumer.addVertex(pose, 0.0F, y, 1.0F)
                .setColor((int)(r*255), (int)(g*255), (int)(b*255), 255)
                .setUv(0.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(light)
                .setNormal(pose, 0.0F, 1.0F, 0.0F);

        poseStack.popPose();
    }

    private boolean isHoldingShovel(Player player) {
        return player.getMainHandItem().is(ItemTags.SHOVELS)
                || player.getOffhandItem().is(ItemTags.SHOVELS);
    }

}
