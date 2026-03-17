package net.blumasc.excitingenchants.datagen;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagProvider extends BlockTagsProvider {
    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ExcitingEnchantsMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Blocks.REDSTONE_TARGET)
                .add(Blocks.PISTON)
                .add(Blocks.STICKY_PISTON)
                .add(Blocks.DISPENSER)
                .add(Blocks.DROPPER)
                .add(Blocks.CRAFTER)
                .add(Blocks.REDSTONE_TORCH)
                .addTag(BlockTags.DOORS)
                .addTag(BlockTags.TRAPDOORS)
                .addTag(BlockTags.PRESSURE_PLATES);

        tag(ModTags.Blocks.SMALL_PLANTS)
                .addTag(BlockTags.SMALL_FLOWERS)
                .addTag(BlockTags.SAPLINGS)
                .add(Blocks.SHORT_GRASS)
                .add(Blocks.FERN)
                .remove(Blocks.SPORE_BLOSSOM);

        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(ModBlocks.CURSE_TABLE.get())
                .add(ModBlocks.GOLDEN_BLOCK.get())
                .add(ModBlocks.GOLDEN_BRICK_SLAB.get())
                .add(ModBlocks.GOLDEN_FENCE.get())
                .add(ModBlocks.GOLDEN_BRICK_WALL.get())
                .add(ModBlocks.GOLDEN_LOG.get())
                .add(ModBlocks.GOLDEN_PLANKS.get())
                .add(ModBlocks.GOLDEN_PLANK_SLAB.get())
                .add(ModBlocks.GOLDEN_PLANK_STAIRS.get())
                .add(ModBlocks.GOLDEN_BRICK_STAIRS.get())
                .add(ModBlocks.GOLDEN_GRASS.get());

        tag(BlockTags.LOGS)
                .add(ModBlocks.GOLDEN_LOG.get());

        tag(BlockTags.PLANKS)
                .add(ModBlocks.GOLDEN_PLANKS.get());

        tag(BlockTags.SLABS)
                .add(ModBlocks.GOLDEN_PLANK_SLAB.get())
                .add(ModBlocks.GOLDEN_BRICK_SLAB.get());

        tag(BlockTags.STAIRS)
                .add(ModBlocks.GOLDEN_PLANK_STAIRS.get())
                .add(ModBlocks.GOLDEN_BRICK_STAIRS.get());

        tag(BlockTags.WALLS)
                .add(ModBlocks.GOLDEN_BRICK_WALL.get());

        tag(BlockTags.FENCES)
                .add(ModBlocks.GOLDEN_FENCE.get());
    }

}
