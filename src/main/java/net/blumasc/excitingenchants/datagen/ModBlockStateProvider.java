package net.blumasc.excitingenchants.datagen;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredBlock;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, ExcitingEnchantsMod.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        blockWithItem(ModBlocks.GOLDEN_BLOCK);
        blockWithItem(ModBlocks.GOLDEN_PLANKS);
        blockWithItem(ModBlocks.GOLDEN_BRICKS);
        stairsBlock(ModBlocks.GOLDEN_BRICK_STAIRS.get(), blockTexture(ModBlocks.GOLDEN_BRICKS.get()));
        blockItem(ModBlocks.GOLDEN_BRICK_STAIRS);
        stairsBlock(ModBlocks.GOLDEN_PLANK_STAIRS.get(), blockTexture(ModBlocks.GOLDEN_PLANKS.get()));
        blockItem(ModBlocks.GOLDEN_PLANK_STAIRS);
        slabBlock(ModBlocks.GOLDEN_BRICK_SLAB.get(), blockTexture(ModBlocks.GOLDEN_BRICKS.get()), blockTexture(ModBlocks.GOLDEN_BRICKS.get()));
        blockItem(ModBlocks.GOLDEN_BRICK_SLAB);
        slabBlock(ModBlocks.GOLDEN_PLANK_SLAB.get(), blockTexture(ModBlocks.GOLDEN_PLANKS.get()), blockTexture(ModBlocks.GOLDEN_PLANKS.get()));
        blockItem(ModBlocks.GOLDEN_PLANK_SLAB);
        wallBlock(ModBlocks.GOLDEN_BRICK_WALL.get(), blockTexture(ModBlocks.GOLDEN_BRICKS.get()));
        fenceBlock(ModBlocks.GOLDEN_FENCE.get(), blockTexture(ModBlocks.GOLDEN_PLANKS.get()));
        axisBlock(ModBlocks.GOLDEN_LOG.get(),
                ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "block/golden_log"),
                ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "block/golden_log_top"));
        blockItem(ModBlocks.GOLDEN_LOG);
        simpleBlock(ModBlocks.GOLDEN_GRASS.get(),
                models().cross(
                        ModBlocks.GOLDEN_GRASS.getRegisteredName(),
                        ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "block/golden_plant_block")
                ).renderType("cutout"));
        itemModels().singleTexture(
                ModBlocks.GOLDEN_GRASS.getRegisteredName(),
                ResourceLocation.withDefaultNamespace("item/generated"),
                "layer0",
                ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "item/golden_plant")
        );
    }

    private void blockWithItem(DeferredBlock<?> deferredBlock){
        simpleBlockWithItem(deferredBlock.get(), cubeAll(deferredBlock.get()));
    }
    private void blockItem(DeferredBlock<?> deferredBlock)
    {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile(ExcitingEnchantsMod.MODID+":block/"+deferredBlock.getId().getPath()));
    }

    private void blockItem(DeferredBlock<?> deferredBlock, String apendix)
    {
        simpleBlockItem(deferredBlock.get(), new ModelFile.UncheckedModelFile(ExcitingEnchantsMod.MODID+":block/"+deferredBlock.getId().getPath()+apendix));
    }
}
