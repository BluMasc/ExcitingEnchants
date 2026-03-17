package net.blumasc.excitingenchants.item.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.ChargedProjectiles;
import org.joml.Matrix4f;

import java.util.List;

public class CrossbowAmmoRenderer extends BlockEntityWithoutLevelRenderer {

    public static final CrossbowAmmoRenderer INSTANCE = new CrossbowAmmoRenderer();

    private static final ResourceLocation ECHO_SHARD_OVERLAY =
        ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/items/overlay/echo_shard.png");
    private static final ResourceLocation LIGHTNING_ROD_OVERLAY =
        ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/items/overlay/lightning_rod.png");
    private static final ResourceLocation BLOOD_SHARD_OVERLAY =
        ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/items/overlay/blood_shard.png");
    private static final ResourceLocation PEBBLES_OVERLAY =
        ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "textures/items/overlay/pebbles.png");

    public CrossbowAmmoRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
              Minecraft.getInstance().getEntityModels());
        System.out.println("CrossbowAmmoRenderer constructed");
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext,
                             PoseStack poseStack, MultiBufferSource bufferSource,
                             int packedLight, int packedOverlay) {

        System.out.println("Rendering Crossbow");

        Minecraft mc = Minecraft.getInstance();

        // Render crossbow normally
        BakedModel model = mc.getItemRenderer().getItemModelShaper().getItemModel(stack);
        mc.getItemRenderer().render(stack, displayContext, false, poseStack,
                bufferSource, packedLight, packedOverlay, model);

        if (!CrossbowItem.isCharged(stack)) return;
        ChargedProjectiles chargedprojectiles = (ChargedProjectiles)stack.get(DataComponents.CHARGED_PROJECTILES);
        if (chargedprojectiles != null && !chargedprojectiles.isEmpty()) {
            ItemStack itemstack = (ItemStack) chargedprojectiles.getItems().get(0);

            System.out.println("Projectile: "+itemstack);

            ResourceLocation overlay = getOverlay(itemstack, mc);
            if (overlay == null) return;

            poseStack.pushPose();

            RenderType renderType = RenderType.entityTranslucentCull(overlay);
            VertexConsumer consumer = bufferSource.getBuffer(renderType);

            Matrix4f matrix = poseStack.last().pose();

            consumer.addVertex(matrix, 0.0f, 0.0f, 0.01f).setColor(255, 255, 255, 255)
                    .setUv(0, 1).setUv2(packedLight & 0xFFFF, packedLight >> 16)
                    .setOverlay(packedOverlay).setNormal(0, 0, 1);
            consumer.addVertex(matrix, 1.0f, 0.0f, 0.01f).setColor(255, 255, 255, 255)
                    .setUv(1, 1).setUv2(packedLight & 0xFFFF, packedLight >> 16)
                    .setOverlay(packedOverlay).setNormal(0, 0, 1);
            consumer.addVertex(matrix, 1.0f, 1.0f, 0.01f).setColor(255, 255, 255, 255)
                    .setUv(1, 0).setUv2(packedLight & 0xFFFF, packedLight >> 16)
                    .setOverlay(packedOverlay).setNormal(0, 0, 1);
            consumer.addVertex(matrix, 0.0f, 1.0f, 0.01f).setColor(255, 255, 255, 255)
                    .setUv(0, 0).setUv2(packedLight & 0xFFFF, packedLight >> 16)
                    .setOverlay(packedOverlay).setNormal(0, 0, 1);

            ((MultiBufferSource.BufferSource) bufferSource).endBatch();

            poseStack.popPose();
        }
    }

    private ResourceLocation getOverlay(ItemStack projectile, Minecraft mc) {
        Item item = projectile.getItem();

        if (item == Items.ECHO_SHARD) return ECHO_SHARD_OVERLAY;
        if (item == Items.LIGHTNING_ROD) return LIGHTNING_ROD_OVERLAY;
        if (item == ModItems.BLOOD_ORB.get()) return BLOOD_SHARD_OVERLAY;

        // Check stone tool materials tag
        if (mc.level != null && projectile.is(
                mc.level.registryAccess()
                        .registryOrThrow(Registries.ITEM)
                        .getTag(ItemTags.STONE_TOOL_MATERIALS)
                        .orElse(null))) {
            return PEBBLES_OVERLAY;
        }

        return null;
    }
}