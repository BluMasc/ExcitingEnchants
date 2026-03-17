package net.blumasc.excitingenchants.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.blumasc.excitingenchants.block.entity.custom.BreakingBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class ReverberatingBlockEntityRenderer
        implements BlockEntityRenderer<BreakingBlockEntity> {

    public ReverberatingBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BreakingBlockEntity entity, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, int packedOverlay) {

        BlockState state = entity.getHeldState();
        if (state == null || state.isAir()) return;

        float progress = entity.getCurrentTime() / (float) entity.getTotalTime();
        int destroyStage = (int)(progress * 9);

        float shake = (float)(Math.sin(
                (entity.getCurrentTime() + partialTick) * 1.5f
        ) * 0.10f * progress)*1.4f;

        poseStack.pushPose();
        poseStack.translate(shake, shake, shake);
        Minecraft.getInstance().getBlockRenderer().renderSingleBlock(
                state,
                poseStack,
                bufferSource,
                packedLight,
                packedOverlay
        );
        poseStack.popPose();
    }
}