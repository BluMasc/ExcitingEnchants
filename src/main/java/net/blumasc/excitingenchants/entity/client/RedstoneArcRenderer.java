package net.blumasc.excitingenchants.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.blumasc.blubasics.entity.custom.projectile.LightningArcEntity;
import net.blumasc.excitingenchants.entity.custom.RedLightning;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;

public class RedstoneArcRenderer extends EntityRenderer<RedLightning> {

    public RedstoneArcRenderer(EntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    public void render(
            RedLightning entity,
            float entityYaw,
            float partialTicks,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
        Vec3 start = vec(entity.getStart());
        Vec3 end   = vec(entity.getEnd());

        Vec3 dir = end.subtract(start);
        double length = dir.length();
        if (length < 0.001) return;

        Vec3 norm = dir.normalize();
        Vec3 camPos = this.entityRenderDispatcher.camera.getPosition();

        Vec3 entityPos = entity.position();
        start = start.subtract(entityPos);
        end   = end.subtract(entityPos);

        VertexConsumer vc = buffer.getBuffer(RenderType.lightning());
        RandomSource rand = RandomSource.create(entity.seed);

        poseStack.pushPose();
        Matrix4f mat = poseStack.last().pose();

        int segments = Math.max(8, (int)(length * 2.0));
        float baseWidth = 0.12f;

        Vec3 prevOffset = Vec3.ZERO;

        for (int i = 0; i < segments; i++) {
            double t1 = (double)i / segments;
            double t2 = (double)(i + 1) / segments;

            Vec3 p1 = start.add(norm.scale(t1 * length));
            Vec3 p2 = start.add(norm.scale(t2 * length));

            Vec3 jitter = new Vec3(
                    (rand.nextFloat() - 0.5) * 0.6,
                    (rand.nextFloat() - 0.5) * 0.6,
                    (rand.nextFloat() - 0.5) * 0.6
            );

            Vec3 a = p1.add(prevOffset);
            Vec3 b = p2.add(jitter);
            prevOffset = jitter;

            Vec3 worldA = a.add(entityPos);
            Vec3 camDir = camPos.subtract(worldA).normalize();
            Vec3 right = norm.cross(camDir).normalize();

            float width = baseWidth * (1.0f - (float)t1 * 0.6f);
            Vec3 w = right.scale(width);

            quad(mat, vc, a, b, w);
        }

        poseStack.popPose();
    }

    private static void quad(
            Matrix4f mat,
            VertexConsumer vc,
            Vec3 a,
            Vec3 b,
            Vec3 w
    ) {
        vc.addVertex(mat,
                (float)(a.x + w.x),
                (float)(a.y + w.y),
                (float)(a.z + w.z)
        ).setColor(1.0f,0.2f, 0.2f,  0.85f);

        vc.addVertex(mat,
                (float)(b.x + w.x),
                (float)(b.y + w.y),
                (float)(b.z + w.z)
        ).setColor(1.0f,0.2f, 0.2f, 0.85f);

        vc.addVertex(mat,
                (float)(b.x - w.x),
                (float)(b.y - w.y),
                (float)(b.z - w.z)
        ).setColor(1.0f,0.2f, 0.2f, 0.85f);

        vc.addVertex(mat,
                (float)(a.x - w.x),
                (float)(a.y - w.y),
                (float)(a.z - w.z)
        ).setColor(1.0f,0.2f, 0.2f, 0.85f);
    }

    private static Vec3 vec(org.joml.Vector3f v) {
        return new Vec3(v.x, v.y, v.z);
    }

    @Override
    public ResourceLocation getTextureLocation(RedLightning entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

    @Override
    public boolean shouldRender(
            RedLightning entity,
            net.minecraft.client.renderer.culling.Frustum frustum,
            double x, double y, double z
    ) {
        return true;
    }
}
