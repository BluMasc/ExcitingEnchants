package net.blumasc.excitingenchants.datagen;

import net.blumasc.blubasics.item.BaseModItems;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.item.ModItems;
import net.blumasc.excitingenchants.util.ModTags;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.conditions.IConditionBuilder;
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.common.conditions.NotCondition;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {
    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    protected void buildRecipes(RecipeOutput recipeOutput) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.CURSE_TABLE.get())
                .pattern("HRH")
                .pattern("ECE")
                .pattern("COC")
                .define('H', BaseModItems.SUN_HORN)
                .define('R', Items.REDSTONE_BLOCK)
                .define('E', Items.EMERALD)
                .define('C', Items.CRYING_OBSIDIAN)
                .define('O', ModBlocks.CURSED_OBSIDIAN)
                .unlockedBy("has_cursed_obsidian", has(ModBlocks.CURSED_OBSIDIAN)).save(recipeOutput);

        fenceBuilder(ModBlocks.GOLDEN_FENCE, Ingredient.of(ModBlocks.GOLDEN_PLANKS))
                .unlockedBy("has_golden_planks", has(ModBlocks.GOLDEN_PLANKS))
                .save(recipeOutput);

        wall(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GOLDEN_BRICK_WALL.get(), ModBlocks.GOLDEN_BRICKS.get());

        stairBuilder(ModBlocks.GOLDEN_BRICK_STAIRS.get(), Ingredient.of(ModBlocks.GOLDEN_BRICKS))
                .unlockedBy("has_golden_bricks", has(ModBlocks.GOLDEN_BRICKS))
                .save(recipeOutput);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GOLDEN_BRICK_SLAB.get(), ModBlocks.GOLDEN_BRICKS.get());

        stairBuilder(ModBlocks.GOLDEN_PLANK_STAIRS.get(), Ingredient.of(ModBlocks.GOLDEN_PLANKS))
                .unlockedBy("has_golden_planks", has(ModBlocks.GOLDEN_PLANKS))
                .save(recipeOutput);
        slab(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GOLDEN_PLANK_SLAB.get(), ModBlocks.GOLDEN_PLANKS.get());

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.GOLDEN_PLANKS, 4)
                .requires(ModBlocks.GOLDEN_LOG)
                .unlockedBy("has_golden_log", has(ModBlocks.GOLDEN_PLANKS))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.HOTDOG, 1)
                .requires(Items.BREAD)
                .requires(Items.BLAZE_POWDER)
                .requires(ModItems.COOKED_AUTHENTIC_MEAT)
                .unlockedBy("has_cooked_authentic_meat", has(ModItems.COOKED_AUTHENTIC_MEAT))
                .save(recipeOutput);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CLEANSING_SOAP, 1)
                .requires(Items.BONE_MEAL)
                .requires(Items.CLAY_BALL)
                .requires(ModBlocks.CURSED_OBSIDIAN)
                .unlockedBy("has_cursed_obsidian", has(ModBlocks.CURSED_OBSIDIAN))
                .save(recipeOutput);

        stonecutterList(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GOLDEN_BLOCK, List.of(
                ModBlocks.GOLDEN_BRICKS,
                ModBlocks.GOLDEN_BRICK_STAIRS,
                ModBlocks.GOLDEN_BRICK_WALL
        ));

        stonecutterList(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GOLDEN_BRICKS, List.of(
                ModBlocks.GOLDEN_BRICK_STAIRS,
                ModBlocks.GOLDEN_BRICK_WALL
        ));

        stonecutting(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GOLDEN_BRICK_SLAB, ModBlocks.GOLDEN_BRICKS, 2);

        stonecutting(recipeOutput, RecipeCategory.BUILDING_BLOCKS, ModBlocks.GOLDEN_BRICK_SLAB, ModBlocks.GOLDEN_BLOCK, 2);

        SmithingTransformRecipeBuilder.smithing(Ingredient.of(new ItemLike[]{Items.RED_WOOL}), Ingredient.of(new ItemLike[]{ModItems.SPEAR_BLANK}), Ingredient.of(new ItemLike[]{Items.GOLD_INGOT}), RecipeCategory.TOOLS, ModItems.SWORDSPEAR.get()).unlocks("has_spear_blank", has(ModItems.SPEAR_BLANK)).save(recipeOutput.withConditions(
                new NotCondition(new ModLoadedCondition("spears"))
        ), ExcitingEnchantsMod.MODID + ":spear_smithing");
        oreSmelting(recipeOutput, Collections.singletonList(ModItems.DULL_SPEAR), RecipeCategory.COMBAT, ModItems.SUPERHEATED_SPEAR, 0.25f, 200, "superheating_spear");
        foodSmelting(recipeOutput, Collections.singletonList(ModItems.RAW_CARP), RecipeCategory.FOOD, ModItems.COOKED_CARP, 0.25f, 200, "cooking_carp");
        foodSmelting(recipeOutput, Collections.singletonList(ModItems.GOLDEN_MEAT), RecipeCategory.FOOD, ModItems.COOKED_GOLDEN_MEAT, 0.25f, 200, "cooking_golden_meat");
        foodSmelting(recipeOutput, Collections.singletonList(ModItems.AUTHENTIC_MEAT), RecipeCategory.FOOD, ModItems.COOKED_AUTHENTIC_MEAT, 0.25f, 200, "cooking_authentic_meat");
        foodSmelting(recipeOutput, Collections.singletonList(ModItems.RAW_RAINBOW_FISH), RecipeCategory.FOOD, ModItems.COOKED_RAINBOW_FISH, 0.25f, 200, "cooking_parrotfish");
        oreSmelting(recipeOutput, List.of(ModBlocks.GOLDEN_BLOCK, ModBlocks.GOLDEN_BRICKS, ModBlocks.GOLDEN_BRICK_WALL, ModBlocks.GOLDEN_BRICK_STAIRS, ModBlocks.GOLDEN_BRICK_SLAB, ModBlocks.GOLDEN_LOG, ModBlocks.GOLDEN_PLANKS, ModBlocks.GOLDEN_FENCE, ModBlocks.GOLDEN_PLANK_SLAB, ModBlocks.GOLDEN_PLANK_STAIRS, ModBlocks.GOLDEN_GRASS, ModItems.COOKED_GOLDEN_MEAT), RecipeCategory.MISC, Items.GOLD_NUGGET, 0.25f, 200, "nuggets_from_gold_stuff");
        itemBlasting(recipeOutput, Collections.singletonList(ModItems.GOLDEN_MEAT), RecipeCategory.MISC, Items.GOLD_NUGGET, 0.25f, 100, "nuggets_from_gold_stuff");
    }

    protected static void itemSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                       float pExperience, int pCookingTIme, String pGroup) {
        itemCooking(recipeOutput, RecipeSerializer.SMELTING_RECIPE, SmeltingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTIme, pGroup, "_from_smelting");
    }

    protected static void itemBlasting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                       float pExperience, int pCookingTime, String pGroup) {
        itemCooking(recipeOutput, RecipeSerializer.BLASTING_RECIPE, BlastingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_blasting");
    }

    protected static void itemSmoking(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        itemCooking(recipeOutput, RecipeSerializer.SMOKING_RECIPE, SmokingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_smoking");
    }
    protected static void itemCampfire(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                       float pExperience, int pCookingTime, String pGroup) {
        itemCooking(recipeOutput, RecipeSerializer.CAMPFIRE_COOKING_RECIPE, CampfireCookingRecipe::new, pIngredients, pCategory, pResult,
                pExperience, pCookingTime, pGroup, "_from_campfire");
    }
    protected static void foodSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                       float pExperience, int pCookingTime, String pGroup) {
        itemSmelting(recipeOutput, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup);
        itemSmoking(recipeOutput, pIngredients, pCategory, pResult, pExperience, pCookingTime/2, pGroup);
        itemCampfire(recipeOutput, pIngredients, pCategory, pResult, pExperience, pCookingTime*3, pGroup);
    }

    protected static void oreSmelting(RecipeOutput recipeOutput, List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult,
                                      float pExperience, int pCookingTime, String pGroup) {
        itemSmelting(recipeOutput, pIngredients, pCategory, pResult, pExperience, pCookingTime, pGroup);
        itemBlasting(recipeOutput, pIngredients, pCategory, pResult, pExperience, pCookingTime/2, pGroup);
    }

    protected static <T extends AbstractCookingRecipe> void itemCooking(RecipeOutput recipeOutput, RecipeSerializer<T> pCookingSerializer, AbstractCookingRecipe.Factory<T> factory,
                                                                        List<ItemLike> pIngredients, RecipeCategory pCategory, ItemLike pResult, float pExperience, int pCookingTime, String pGroup, String pRecipeName) {
        for(ItemLike itemlike : pIngredients) {
            SimpleCookingRecipeBuilder.generic(Ingredient.of(itemlike), pCategory, pResult, pExperience, pCookingTime, pCookingSerializer, factory).group(pGroup).unlockedBy(getHasName(itemlike), has(itemlike))
                    .save(recipeOutput, ExcitingEnchantsMod.MODID + ":smelting/" + getItemName(pResult) + pRecipeName + "_" + getItemName(itemlike));
        }
    }

    protected static void stonecutterList(RecipeOutput recipeOutput, RecipeCategory recipeCategory, ItemLike input, List<ItemLike> outputs)
    {
        for (ItemLike e : outputs)
        {
            stonecutting(recipeOutput, recipeCategory, e, input);
        }
    }

    protected static void stonecutting(RecipeOutput recipeOutput, RecipeCategory recipeCategory, ItemLike output, ItemLike input){
        stonecutting(recipeOutput, recipeCategory, output, input, 1);
    }
    protected static void stonecutting(RecipeOutput recipeOutput, RecipeCategory category, ItemLike result, ItemLike material, int resultCount) {
        SingleItemRecipeBuilder recipeBuilder = SingleItemRecipeBuilder.stonecutting(Ingredient.of(new ItemLike[]{material}), category, result, resultCount).unlockedBy(getHasName(material), has(material));
        String recipeName = getConversionRecipeName(result, material);
        recipeBuilder.save(recipeOutput, ExcitingEnchantsMod.MODID+":stonecutting/"+recipeName + "_stonecutting");
    }
}
