package net.blumasc.excitingenchants.entity.client;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.entity.custom.BloodShardProjectile;
import net.blumasc.excitingenchants.entity.custom.WoodArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class BloodShardRenderer extends ArrowRenderer<BloodShardProjectile> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/blood_shard/texture.png");

    public BloodShardRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(BloodShardProjectile entity) {
        return TEXTURE;
    }
}
