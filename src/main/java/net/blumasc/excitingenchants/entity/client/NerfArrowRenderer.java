package net.blumasc.excitingenchants.entity.client;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.entity.custom.NerfArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class NerfArrowRenderer extends ArrowRenderer<NerfArrow> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/nerf_arrow/texture.png");

    public NerfArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(NerfArrow entity) {
        return TEXTURE;
    }
}
