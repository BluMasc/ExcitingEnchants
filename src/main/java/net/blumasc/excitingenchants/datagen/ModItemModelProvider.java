package net.blumasc.excitingenchants.datagen;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.item.ModItems;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, ExcitingEnchantsMod.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModBlocks.PITFALL_TRAP.asItem());
        basicItem(ModItems.BLOOD_ORB.get());
        basicItem(ModItems.GASSTATION_SUSHI.get());
        basicItem(ModItems.RAW_CARP.get());
        basicItem(ModItems.COOKED_CARP.get());
        basicItem(ModItems.RAW_RAINBOW_FISH.get());
        basicItem(ModItems.COOKED_RAINBOW_FISH.get());
        basicItem(ModItems.HOTDOG.get());
        basicItem(ModItems.GOLDEN_MEAT.get());
        basicItem(ModItems.COOKED_GOLDEN_MEAT.get());
        basicItem(ModItems.AUTHENTIC_MEAT.get());
        basicItem(ModItems.COOKED_AUTHENTIC_MEAT.get());
        basicItem(ModItems.RARE_FISH.get());
        basicItem(ModItems.CLEANSING_SOAP.get());
        handheldItem(ModItems.BIDENT.get());
        handheldItem(ModItems.OVERGROWN_SPEAR.get());
        handheldItem(ModItems.RUSTED_SPEAR.get());
        handheldItem(ModItems.DULL_SPEAR.get());
        handheldItem(ModItems.SUPERHEATED_SPEAR.get());
        handheldItem(ModItems.MELLEABLE_SPEAR.get());
        handheldItem(ModItems.REPAIRED_SPEAR.get());
        handheldItem(ModItems.SPEAR_BLANK.get());
        wallItem(ModBlocks.GOLDEN_BRICK_WALL, ModBlocks.GOLDEN_BRICKS);

        fenceItem(ModBlocks.GOLDEN_FENCE, ModBlocks.GOLDEN_PLANKS);


    }
    public void wallItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/wall_inventory"))
                .texture("wall",  ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID,
                        "block/" + baseBlock.getId().getPath()));
    }

    public void fenceItem(DeferredBlock<?> block, DeferredBlock<Block> baseBlock) {
        this.withExistingParent(block.getId().getPath(), mcLoc("block/fence_inventory"))
                .texture("texture",  ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID,
                        "block/" + baseBlock.getId().getPath()));
    }
}
