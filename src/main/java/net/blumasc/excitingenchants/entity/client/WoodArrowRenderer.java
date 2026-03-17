package net.blumasc.excitingenchants.entity.client;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.entity.custom.WoodArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class WoodArrowRenderer extends ArrowRenderer<WoodArrow> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/wood_arrow/texture.png");

    public WoodArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(WoodArrow entity) {
        return TEXTURE;
    }
}
