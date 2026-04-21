package net.blumasc.excitingenchants.datagen;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.item.ModItems;
import net.blumasc.excitingenchants.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> blockTags, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, ExcitingEnchantsMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        tag(ModTags.Items.ENCHANTABLE_DROP_EDITOR)
                .addTag(ItemTags.MINING_ENCHANTABLE)
                .addTag(ItemTags.FIRE_ASPECT_ENCHANTABLE);

        tag(ModTags.Items.HEAD_FEET)
                .addTag(ItemTags.HEAD_ARMOR_ENCHANTABLE)
                .addTag(ItemTags.FOOT_ARMOR_ENCHANTABLE);

        tag(ModTags.Items.ENCHANTABLE_SHOOTING)
                .addTag(ItemTags.BOW_ENCHANTABLE)
                .addTag(ItemTags.CROSSBOW_ENCHANTABLE);
        tag(ModTags.Items.GRAPESHOT_AMMUNITION)
                .addTag(ItemTags.STONE_TOOL_MATERIALS);
        tag(ModTags.Items.NATURALLY_CURSED)
                .add(ModItems.HOTDOG.get())
                .add(ModItems.AUTHENTIC_MEAT.get())
                .add(ModItems.COOKED_AUTHENTIC_MEAT.get())
                .add(ModItems.BIDENT.get())
                .add(ModItems.RAW_CARP.get())
                .add(ModItems.RAW_RAINBOW_FISH.get())
                .add(ModItems.RARE_FISH.get())
                .add(ModItems.GOLDEN_MEAT.get())
                .add(ModItems.GASSTATION_SUSHI.get())
                .add(ModBlocks.GOLDEN_BLOCK.asItem())
                .add(ModBlocks.GOLDEN_GRASS.asItem())
                .add(Items.PUFFERFISH);

        tag(ItemTags.TRIDENT_ENCHANTABLE)
                .add(ModItems.BIDENT.get());

        tag(ItemTags.SWORD_ENCHANTABLE)
                .add(ModItems.SWORDSPEAR.get());

        tag(ItemTags.SWORDS)
                .add(ModItems.SWORDSPEAR.get());

        tag(ItemTags.LOGS)
                .add(ModBlocks.GOLDEN_LOG.asItem());

        tag(ItemTags.PLANKS)
                .add(ModBlocks.GOLDEN_PLANKS.asItem());

        tag(ItemTags.SLABS)
                .add(ModBlocks.GOLDEN_PLANK_SLAB.asItem())
                .add(ModBlocks.GOLDEN_BRICK_SLAB.asItem());

        tag(ItemTags.STAIRS)
                .add(ModBlocks.GOLDEN_PLANK_STAIRS.asItem())
                .add(ModBlocks.GOLDEN_BRICK_STAIRS.asItem());

        tag(ItemTags.WALLS)
                .add(ModBlocks.GOLDEN_BRICK_WALL.asItem());

        tag(ItemTags.FENCES)
                .add(ModBlocks.GOLDEN_FENCE.asItem());

        tag(ItemTags.MEAT)
                .add(ModItems.GOLDEN_MEAT.asItem())
                .add(ModItems.COOKED_GOLDEN_MEAT.asItem())
                .add(ModItems.AUTHENTIC_MEAT.asItem())
                .add(ModItems.COOKED_AUTHENTIC_MEAT.asItem());

        tag(ItemTags.FISHES)
                .add(ModItems.RAW_CARP.asItem())
                .add(ModItems.COOKED_CARP.get())
                .add(ModItems.RAW_RAINBOW_FISH.asItem())
                .add(ModItems.COOKED_RAINBOW_FISH.get())
                .add(ModItems.RARE_FISH.asItem());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("minecraft","spears")))
                .add(ModItems.SWORDSPEAR.get());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c","foods/raw_meat")))
                .add(ModItems.GOLDEN_MEAT.get())
                .add(ModItems.AUTHENTIC_MEAT.get());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c","foods/cooked_meat")))
                .add(ModItems.COOKED_GOLDEN_MEAT.get())
                .add(ModItems.COOKED_AUTHENTIC_MEAT.get());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c","foods")))
                .add(ModItems.HOTDOG.get());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c","foods/raw_fish")))
                .add(ModItems.RAW_CARP.get())
                .add(ModItems.RAW_RAINBOW_FISH.get())
                .add(ModItems.RARE_FISH.get());

        tag(ItemTags.create(ResourceLocation.fromNamespaceAndPath("c","foods/cooked_fish")))
                .add(ModItems.COOKED_CARP.get())
                .add(ModItems.COOKED_RAINBOW_FISH.get());

        tag(ItemTags.OCELOT_FOOD)
                .add(ModItems.RAW_CARP.get())
                .add(ModItems.RAW_RAINBOW_FISH.get())
                .add(ModItems.RARE_FISH.get());

        tag(ItemTags.CAT_FOOD)
                .add(ModItems.RAW_CARP.get())
                .add(ModItems.RAW_RAINBOW_FISH.get())
                .add(ModItems.RARE_FISH.get());

        tag(ModTags.Items.GHOUL_EATABLE)
                .add(ModItems.AUTHENTIC_MEAT.get())
                .add(ModItems.COOKED_AUTHENTIC_MEAT.get())
                .add(ModItems.HOTDOG.get())
                .add(Items.ROTTEN_FLESH);


    }
}
