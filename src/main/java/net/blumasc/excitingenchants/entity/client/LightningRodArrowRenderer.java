package net.blumasc.excitingenchants.entity.client;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.entity.custom.LightningRodArrowEntity;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class LightningRodArrowRenderer extends ArrowRenderer<LightningRodArrowEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/lightning_rod_arrow/texture.png");

    public LightningRodArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(LightningRodArrowEntity entity) {
        return TEXTURE;
    }
}
