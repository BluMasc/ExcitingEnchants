package net.blumasc.excitingenchants.entity.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;

public class ProjectileEmptyRenderer extends EntityRenderer<Projectile> {

    public ProjectileEmptyRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(
            Projectile entity,
            float entityYaw,
            float partialTick,
            PoseStack poseStack,
            MultiBufferSource buffer,
            int packedLight
    ) {
    }

    @Override
    public ResourceLocation getTextureLocation(Projectile entity) {
        return null; // not used
    }
}
