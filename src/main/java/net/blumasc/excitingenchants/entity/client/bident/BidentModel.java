package net.blumasc.excitingenchants.entity.client.bident;// Made with Blockbench 5.0.7
// Exported for Minecraft version 1.17 or later with Mojang mappings
// Paste this class into your mod and generate all required imports


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class BidentModel<T extends Entity> extends EntityModel<T> {
	// This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
	public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "bident_converted"), "main");
	private final ModelPart bone;

	public BidentModel(ModelPart root) {
		this.bone = root.getChild("bone");
	}

	public static LayerDefinition createBodyLayer() {
		MeshDefinition meshdefinition = new MeshDefinition();
		PartDefinition partdefinition = meshdefinition.getRoot();

		PartDefinition bone = partdefinition.addOrReplaceChild("bone", CubeListBuilder.create().texOffs(0, 0).addBox(-9.0F, -6.0F, 7.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 15).addBox(-7.0F, -4.0F, 8.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(12, 15).addBox(-11.0F, -6.0F, 8.0F, 2.0F, 4.0F, 0.0F, new CubeDeformation(0.0F))
		.texOffs(8, 0).addBox(-7.0F, -17.0F, 7.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(0, 12).addBox(-11.0F, -17.0F, 7.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F))
		.texOffs(8, 12).addBox(-10.0F, -7.0F, 7.0F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(8.0F, 24.0F, -8.0F));

		return LayerDefinition.create(meshdefinition, 32, 32);
	}

	@Override
	public void setupAnim(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

	}

	@Override
	public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
		poseStack.pushPose();
		poseStack.translate(0, -0.6, 0);
		poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
		poseStack.mulPose(Axis.YP.rotationDegrees(90.0F));
		poseStack.mulPose(Axis.ZP.rotationDegrees(-90.0F));
		bone.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
		poseStack.popPose();
	}
}