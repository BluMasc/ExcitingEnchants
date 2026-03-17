package net.blumasc.excitingenchants.entity.client.balloon;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.entity.custom.BalloonEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class BalloonRenderer extends LivingEntityRenderer<BalloonEntity, BalloonModel<BalloonEntity>> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/balloon/texture.png");

    public BalloonRenderer(EntityRendererProvider.Context context) {
        super(context, new BalloonModel<>(context.bakeLayer(BalloonModel.LAYER_LOCATION)), 0.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(BalloonEntity entity) {
        return TEXTURE;
    }

    @Override
    public void render(BalloonEntity entity, float entityYaw, float partialTick,
                       PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();
        poseStack.mulPose(Axis.ZP.rotationDegrees(180));
        poseStack.translate(0, -1.5, 0);
        setupRotations(entity, poseStack, entity.tickCount + partialTick, entityYaw, partialTick, 1.0f);
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        this.model.renderToBuffer(poseStack, consumer, packedLight,
                getOverlayCoords(entity, 0.0f),
                entity.getColor());
        poseStack.popPose();

        Entity target = entity.getBoundEntity();
        if (target != null) {
            renderBalloonString(entity, target, partialTick, poseStack, bufferSource, packedLight);
        }
    }

    @Override
    protected boolean shouldShowName(BalloonEntity entity) {
        return false;
    }

    private void renderBalloonString(BalloonEntity balloon, Entity target,
                                     float partialTick, PoseStack poseStack,
                                     MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        Vec3 balloonPos = new Vec3(
                Mth.lerp(partialTick, balloon.xo, balloon.getX()),
                Mth.lerp(partialTick, balloon.yo, balloon.getY()),
                Mth.lerp(partialTick, balloon.zo, balloon.getZ())
        );

        Vec3 targetPos = new Vec3(
                Mth.lerp(partialTick, target.xo, target.getX()),
                Mth.lerp(partialTick, target.yo, target.getY()) + target.getBbHeight(),
                Mth.lerp(partialTick, target.zo, target.getZ())
        );

        Vec3 offset = targetPos.subtract(balloonPos);

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer lineConsumer = bufferSource.getBuffer(RenderType.leash());

        int segments = 24;
        for (int i = 0; i < segments; i++) {
            float t     = i       / (float) segments;
            float tNext = (i + 1) / (float) segments;
            float x0 = (float)(offset.x * t);
            float y0 = (float)(offset.y * t);
            float z0 = (float)(offset.z * t);

            float x1 = (float)(offset.x * tNext);
            float y1 = (float)(offset.y * tNext);
            float z1 = (float)(offset.z * tNext);
            float brightness0 = Mth.lerp(t,     1.0f, 0.5f);
            float brightness1 = Mth.lerp(tNext, 1.0f, 0.5f);

            int light0 = packLight(brightness0);
            int light1 = packLight(brightness1);

            lineConsumer.addVertex(matrix, x0 + 0.025f, y0, z0)
                    .setColor(brightness0, brightness0, brightness0, 1.0f)
                    .setUv(0.5f, t).setLight(light0);
            lineConsumer.addVertex(matrix, x1 + 0.025f, y1, z1)
                    .setColor(brightness1, brightness1, brightness1, 1.0f)
                    .setUv(0.5f, tNext).setLight(light1);
            lineConsumer.addVertex(matrix, x1 - 0.025f, y1, z1)
                    .setColor(brightness1, brightness1, brightness1, 1.0f)
                    .setUv(0.5f, tNext).setLight(light1);
            lineConsumer.addVertex(matrix, x0 - 0.025f, y0, z0)
                    .setColor(brightness0, brightness0, brightness0, 1.0f)
                    .setUv(0.5f, t).setLight(light0);
        }

        poseStack.popPose();
    }

    private static int packLight(float brightness) {
        int b = (int) (brightness * 15);
        return b | (b << 16);
    }
}