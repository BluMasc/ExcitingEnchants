package net.blumasc.excitingenchants.entity.client.echoghost;

import com.mojang.blaze3d.vertex.PoseStack;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.entity.custom.EchoGhostEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.resources.ResourceLocation;

public class EchoGhostRenderer extends MobRenderer<EchoGhostEntity, EchoGhostModel<EchoGhostEntity>> {

    public static final ResourceLocation GHOST_TEXTURE = ResourceLocation.fromNamespaceAndPath(
        ExcitingEnchantsMod.MODID, "textures/entity/echo_ghost.png");

    public EchoGhostRenderer(EntityRendererProvider.Context ctx) {
        super(ctx,
            new EchoGhostModel<>(ctx.bakeLayer(EchoGhostModel.LAYER_LOCATION)),
            0.0F);

        this.addLayer(new ItemInHandLayer<>(this, ctx.getItemInHandRenderer()));
    }

    @Override
    public ResourceLocation getTextureLocation(EchoGhostEntity entity) {
        return GHOST_TEXTURE;
    }

    @Override
    public void render(EchoGhostEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        double bob = Math.sin((entity.tickCount + partialTick) * 0.15) * 0.08;
        poseStack.translate(0, bob, 0);

        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);

        poseStack.popPose();
    }
}