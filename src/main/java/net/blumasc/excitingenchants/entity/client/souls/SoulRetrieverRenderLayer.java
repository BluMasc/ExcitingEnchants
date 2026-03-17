package net.blumasc.excitingenchants.entity.client.souls;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.blumasc.excitingenchants.client.ClientSoulData;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoulRetrieverRenderLayer<T extends Player>
        extends RenderLayer<T, PlayerModel<T>> {

    private final EntityRenderDispatcher entityRenderDispatcher;
    private final static Map<ResourceLocation, LivingEntity> entityCache = new HashMap<>();

    public SoulRetrieverRenderLayer(RenderLayerParent<T, PlayerModel<T>> parent) {
        super(parent);
        this.entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource,
                       int packedLight, T player, float limbSwing,
                       float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        Holder<Enchantment> soulRetrieverHolder = player.level().registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.SOUL_RETRIEVER);

        ItemStack weapon = player.getMainHandItem();
        int level = weapon.getEnchantmentLevel(soulRetrieverHolder);
        if (level <= 0) return;

        List<ClientSoulData.SoulOrbit> souls =
            ClientSoulData.playerSouls.get(player.getUUID());
        if (souls == null || souls.isEmpty()) return;

        for (int i = 0; i < souls.size(); i++) {
            ClientSoulData.SoulOrbit soul = souls.get(i);
            float baseAngle = (float)(i * (2 * Math.PI / souls.size()));
            float angle = baseAngle + (ageInTicks * 0.05f);
            float radius = 0.8f;
            float x = (float) Math.cos(angle) * radius;
            float z = (float) Math.sin(angle) * radius;
            float y = soul.orbitHeight() + (float) Math.sin(ageInTicks * 0.03f + i) * 0.15f;
            LivingEntity dummy = getOrCreateDummy(soul.entityType(), player);
            if (dummy == null) continue;

            poseStack.pushPose();
            poseStack.translate(x, y, z);
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.mulPose(Axis.YP.rotationDegrees((float) Math.toDegrees(angle) + 270f));
            poseStack.scale(0.2f, 0.2f, 0.2f);
            renderSoulEntity(dummy, poseStack, bufferSource, 15728880, partialTick);

            poseStack.popPose();
        }
    }

    private void renderSoulEntity(LivingEntity entity, PoseStack poseStack,
                                   MultiBufferSource bufferSource, int packedLight,
                                   float partialTick) {
        MultiBufferSource tintedBuffer = renderType -> {
            VertexConsumer base = bufferSource.getBuffer(renderType);
            return new SoulVertexConsumer(base);
        };

        entityRenderDispatcher.render(
            entity, 0, 0, 0, 0, partialTick,
            poseStack, tintedBuffer, packedLight
        );
    }

    public static LivingEntity getOrCreateDummy(ResourceLocation entityType, Player player) {
        return entityCache.computeIfAbsent(entityType, key -> {
            EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(key);
            if (type == null) return null;
            Entity entity = type.create(player.level());
            if (entity instanceof LivingEntity living) return living;
            return null;
        });
    }
}