package net.blumasc.excitingenchants.entity.client.castle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.effect.ModEffects;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CastleRenderLayer<T extends Player> extends RenderLayer<T, PlayerModel<T>> {

    private static final ResourceLocation[] CASTLE_TEXTURES = {
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/castle/castle_dirt.png"),
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/castle/castle_wood.png"),
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/castle/castle_stone.png"),
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/castle/castle_deepslate.png"),
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/castle/castle_obsidian.png")
    };

    private final CastleModel castleModel;

    public CastleRenderLayer(RenderLayerParent<T, PlayerModel<T>> parent,
                             EntityModelSet modelSet) {
        super(parent);
        this.castleModel = new CastleModel(modelSet.bakeLayer(CastleModel.LAYER_LOCATION));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, T player,
                       float limbSwing, float limbSwingAmount,
                       float partialTick, float ageInTicks,
                       float netHeadYaw, float headPitch) {

        if (!player.hasEffect(ModEffects.CASTLE_MODE)) return;

        int effect = player.getEffect(ModEffects.CASTLE_MODE).getAmplifier();

        ResourceLocation texture = CASTLE_TEXTURES[effect%CASTLE_TEXTURES.length];

        VertexConsumer consumer = bufferSource.getBuffer(
            RenderType.entityCutoutNoCull(texture)
        );
        castleModel.renderToBuffer(poseStack, consumer, packedLight,
                OverlayTexture.NO_OVERLAY);
    }
}