package net.blumasc.excitingenchants.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.blumasc.blubasics.item.BaseModItems;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.entity.custom.CurseTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

public class CurseTableBlockEntityRenderer implements BlockEntityRenderer<CurseTableBlockEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/horn/texture.png");

    private static final ResourceLocation TEXTURE_CHARGED =
            ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/entity/horn/texture_charged.png");

    public static HornModel<LivingEntity> MODEL;

    public CurseTableBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        ModelPart part = Minecraft.getInstance().getEntityModels().bakeLayer(HornModel.LAYER_LOCATION);
        MODEL = new HornModel<>(part);
    }

    @Override
    public void render(CurseTableBlockEntity altarBlockEntity, float pPartialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int pPackedLight, int pPactOverlay) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        ItemStack stack = altarBlockEntity.inventory.getStackInSlot(0);

        poseStack.pushPose();
        poseStack.translate(0.5f, 0.9f, 0.5f);
        poseStack.scale(0.25f,0.25f,0.25f);
        poseStack.mulPose(Axis.YP.rotationDegrees(altarBlockEntity.getRenderingRotation()));

        int packedLight = getLightLevel(altarBlockEntity.getLevel(),altarBlockEntity.getBlockPos());

        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, packedLight ,
                OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource, altarBlockEntity.getLevel(),1);
        poseStack.popPose();

        renderCornerItem(altarBlockEntity, altarBlockEntity.inventory.getStackInSlot(1), poseStack, multiBufferSource, packedLight,
                0.53f, 0.19f, 270);

        renderCornerItem(altarBlockEntity, altarBlockEntity.inventory.getStackInSlot(2), poseStack, multiBufferSource, packedLight,
                0.47f, 0.81f, 90);

        renderCornerItem(altarBlockEntity, altarBlockEntity.inventory.getStackInSlot(3), poseStack, multiBufferSource, packedLight,
                0.19f, 0.47f, 180);

        renderCornerItem(altarBlockEntity, altarBlockEntity.inventory.getStackInSlot(4), poseStack, multiBufferSource, packedLight,
                0.81f, 0.53f, 0);
    }

    private int getLightLevel(Level level, BlockPos pos){
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }

    private void renderCornerItem(CurseTableBlockEntity entity, ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int light,
                                  float xOffset, float zOffset, float yRot) {

        if (stack.isEmpty()) return;

        ResourceLocation texture = stack.is(BaseModItems.SUN_HORN)? TEXTURE_CHARGED:TEXTURE;

        poseStack.pushPose();

        poseStack.translate(xOffset, 2.1f, zOffset);

        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        poseStack.mulPose(Axis.YP.rotationDegrees(yRot));

        var vertexConsumer = buffer.getBuffer(MODEL.renderType(texture));
        MODEL.renderToBuffer(poseStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();
    }
}
