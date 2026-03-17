package net.blumasc.excitingenchants.compat;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.List;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new SpearRecipeCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        IVanillaRecipeFactory factory = registration.getVanillaRecipeFactory();

        IJeiAnvilRecipe recipe = factory.createAnvilRecipe(
                new ItemStack(ModItems.MELLEABLE_SPEAR.get()),
                List.of(new ItemStack(Items.IRON_INGOT, 3)),
                List.of(new ItemStack(ModItems.REPAIRED_SPEAR.get()))
        );

        registration.addRecipes(RecipeTypes.ANVIL, List.of(recipe));
        registration.addRecipes(SpearRecipeCategory.TYPE, List.of(
                new SpearRecipe(new ItemStack(ModItems.OVERGROWN_SPEAR.get()),  new ItemStack(Items.SHEARS),new ItemStack(ModItems.RUSTED_SPEAR.get())),
                new SpearRecipe(new ItemStack(ModItems.RUSTED_SPEAR.get()),     new ItemStack(Items.GRINDSTONE),new ItemStack(ModItems.DULL_SPEAR.get())),
                new SpearRecipe(new ItemStack(ModItems.SUPERHEATED_SPEAR.get()),new ItemStack(Items.WATER_BUCKET),new ItemStack(ModItems.MELLEABLE_SPEAR.get())),
                new SpearRecipe(new ItemStack(ModItems.REPAIRED_SPEAR.get()),   new ItemStack(Items.POWDER_SNOW_BUCKET),new ItemStack(ModItems.SPEAR_BLANK.get()))
        ));
    }
}