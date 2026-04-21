package net.blumasc.excitingenchants.datagen;

import net.blumasc.excitingenchants.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    protected ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropOther(ModBlocks.INK_LAYER_BLOCK.get(), Items.BLACK_DYE);
        dropSelf(ModBlocks.CURSE_TABLE.get());
        add(ModBlocks.GOLDEN_PLANK_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.GOLDEN_PLANK_SLAB.get()));
        add(ModBlocks.GOLDEN_BRICK_SLAB.get(),
                block -> createSlabItemTable(ModBlocks.GOLDEN_BRICK_SLAB.get()));
        dropSelf(ModBlocks.GOLDEN_BLOCK.get());
        dropSelf(ModBlocks.GOLDEN_GRASS.get());
        dropSelf(ModBlocks.GOLDEN_BRICK_WALL.get());
        dropSelf(ModBlocks.GOLDEN_BRICK_STAIRS.get());
        dropSelf(ModBlocks.GOLDEN_BRICKS.get());
        dropSelf(ModBlocks.GOLDEN_LOG.get());
        dropSelf(ModBlocks.GOLDEN_PLANKS.get());
        dropSelf(ModBlocks.GOLDEN_PLANK_STAIRS.get());
        dropSelf(ModBlocks.GOLDEN_FENCE.get());
        dropSelf(ModBlocks.CURSED_OBSIDIAN.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(
                ModBlocks.INK_LAYER_BLOCK.get(),
                ModBlocks.CURSE_TABLE.get(),
                ModBlocks.GOLDEN_BRICK_WALL.get(),
                ModBlocks.GOLDEN_BRICK_STAIRS.get(),
                ModBlocks.GOLDEN_BRICK_SLAB.get(),
                ModBlocks.GOLDEN_LOG.get(),
                ModBlocks.GOLDEN_PLANKS.get(),
                ModBlocks.GOLDEN_FENCE.get(),
                ModBlocks.GOLDEN_PLANK_SLAB.get(),
                ModBlocks.GOLDEN_PLANK_STAIRS.get(),
                ModBlocks.GOLDEN_GRASS.get(),
                ModBlocks.GOLDEN_BLOCK.get(),
                ModBlocks.GOLDEN_BRICKS.get(),
                ModBlocks.GOLDEN_BRICK_WALL.get(),
                ModBlocks.CURSED_OBSIDIAN.get()
        );

    }
}
