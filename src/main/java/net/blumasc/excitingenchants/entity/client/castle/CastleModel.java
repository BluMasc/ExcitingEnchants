package net.blumasc.excitingenchants.entity.client.castle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class CastleModel<T extends Entity> extends EntityModel<T> {
    // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "castle"), "main");
    private final ModelPart bb_main;

    public CastleModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(24, 35).addBox(-5.0F, -7.0F, -7.0F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).addBox(-5.0F, -7.0F, 5.0F, 10.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 9).addBox(-8.0F, -10.0F, -8.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(24, 22).addBox(5.0F, -10.0F, -8.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 34).addBox(-8.0F, -10.0F, 5.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(12, 34).addBox(5.0F, -10.0F, 5.0F, 3.0F, 10.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-7.0F, -7.0F, -5.0F, 2.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 17).addBox(5.0F, -7.0F, -5.0F, 2.0F, 7.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(36, 9).addBox(1.0F, -7.0F, -7.0F, 4.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(36, 18).addBox(-1.0F, -7.0F, -7.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(36, 24).addBox(-6.0F, -11.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 26).addBox(-8.0F, -11.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 28).addBox(-6.0F, -11.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 30).addBox(-8.0F, -11.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 32).addBox(5.0F, -11.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 34).addBox(7.0F, -11.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 36).addBox(5.0F, -11.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 38).addBox(7.0F, -11.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 24).addBox(-8.0F, -11.0F, 7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 26).addBox(-6.0F, -11.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 28).addBox(-8.0F, -11.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 30).addBox(-6.0F, -11.0F, 7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 32).addBox(5.0F, -11.0F, 7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 34).addBox(7.0F, -11.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 40).addBox(5.0F, -11.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 36).addBox(7.0F, -11.0F, 7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 38).addBox(-4.0F, -8.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 40).addBox(-2.0F, -8.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 42).addBox(1.0F, -8.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(40, 42).addBox(3.0F, -8.0F, -7.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 18).addBox(-4.0F, -8.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 20).addBox(-2.0F, -8.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 22).addBox(1.0F, -8.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(24, 44).addBox(3.0F, -8.0F, 6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 24).addBox(-7.0F, -8.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 26).addBox(-7.0F, -8.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 44).addBox(-7.0F, -8.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 28).addBox(-7.0F, -8.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 30).addBox(6.0F, -8.0F, -4.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 44).addBox(6.0F, -8.0F, -2.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 32).addBox(6.0F, -8.0F, 3.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(44, 34).addBox(6.0F, -8.0F, 1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }


    @Override
    public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }
}
