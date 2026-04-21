package net.blumasc.excitingenchants.block;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.custom.*;
import net.blumasc.excitingenchants.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(ExcitingEnchantsMod.MODID);

    public static final DeferredBlock<TrapBlock> PITFALL_TRAP = registerBlock("pitfall_trap",
            () -> new TrapBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT).noOcclusion()));

    public static final DeferredBlock<CurseTableBlock> CURSE_TABLE = registerBlock("curse_table",
            () -> new CurseTableBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.ENCHANTING_TABLE).noOcclusion()));

    public static final DeferredBlock<BreakingBlock> BREAKING_BLOCK = registerBlock("breaking_block",
            () -> new BreakingBlock(BlockBehaviour.Properties.of().noOcclusion()));

    public static final DeferredBlock<InkLayerBlock> INK_LAYER_BLOCK = registerBlock("ink_layer",
            () -> new InkLayerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SLIME_BLOCK).noOcclusion().replaceable()));

    public static final DeferredBlock<CloudBlock> CLOUD_BLOCK = registerBlock("cloud_block",
            () -> new CloudBlock(BlockBehaviour.Properties.of()
                    .strength(0.1f)
                    .noOcclusion()
                    .isSuffocating((state, level, pos) -> false)
                    .isViewBlocking((state, level, pos) -> false)
                    .randomTicks()
                    .sound(SoundType.WOOL)));

    public static final DeferredBlock<Block> GOLDEN_BLOCK = registerBlock("golden_block",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK)));
    public static final DeferredBlock<Block> GOLDEN_BRICKS = registerBlock("golden_bricks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK)));
    public static final DeferredBlock<Block> GOLDEN_PLANKS = registerBlock("golden_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).sound(SoundType.METAL).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<RotatedPillarBlock> GOLDEN_LOG = registerBlock("golden_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).sound(SoundType.METAL).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<FenceBlock> GOLDEN_FENCE = registerBlock("golden_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GOLD_BLOCK).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<StairBlock> GOLDEN_BRICK_STAIRS = registerBlock("golden_brick_staris",
            () -> new StairBlock(GOLDEN_BRICKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.BRICK_STAIRS).sound(SoundType.METAL).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<SlabBlock> GOLDEN_BRICK_SLAB = registerBlock("golden_brick_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STONE_SLAB).sound(SoundType.METAL).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<StairBlock> GOLDEN_PLANK_STAIRS = registerBlock("golden_plank_staris",
            () -> new StairBlock(GOLDEN_PLANKS.get().defaultBlockState(),
                    BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS).sound(SoundType.METAL).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<SlabBlock> GOLDEN_PLANK_SLAB = registerBlock("golden_plank_slab",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).sound(SoundType.METAL).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<WallBlock> GOLDEN_BRICK_WALL = registerBlock("golden_brick_wall",
            () -> new WallBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DEEPSLATE_BRICK_WALL).sound(SoundType.METAL).mapColor(MapColor.COLOR_YELLOW)));
    public static final DeferredBlock<TallGrassBlock> GOLDEN_GRASS = registerBlock("golden_grass",
            () -> new TallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS).sound(SoundType.METAL).mapColor(MapColor.COLOR_YELLOW)));

    public static final DeferredBlock<Block> CURSED_OBSIDIAN = registerBlock("cursed_obsidian",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OBSIDIAN)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block){
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block)
    {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}