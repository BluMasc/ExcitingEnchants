package net.blumasc.excitingenchants.compat;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.item.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class SpearRecipeCategory implements IRecipeCategory<SpearRecipe> {

    public static final RecipeType<SpearRecipe> TYPE = RecipeType.create(ExcitingEnchantsMod.MODID, "spear_crafting", SpearRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public SpearRecipeCategory(IGuiHelper helper) {
        this.background = helper.createBlankDrawable(120, 26);
        this.icon = helper.createDrawableItemStack(new ItemStack(ModItems.SWORDSPEAR.get()));
    }

    @Override
    public RecipeType<SpearRecipe> getRecipeType() { return TYPE; }

    @Override
    public Component getTitle() {
        return Component.translatable("jei."+ExcitingEnchantsMod.MODID+".spear_crafting");
    }

    @Override
    public IDrawable getBackground() { return background; }

    @Override
    public IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SpearRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 5)
            .addItemStack(recipe.input());

        builder.addSlot(RecipeIngredientRole.INPUT, 20, 5)
                .addItemStack(recipe.catalyst());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 79, 5)
            .addItemStack(recipe.output());
    }

    @Override
    public void draw(SpearRecipe recipe, IRecipeSlotsView slots, GuiGraphics graphics, double mouseX, double mouseY) {
        graphics.drawString(Minecraft.getInstance().font, "→", 52, 10, 0x404040, false);
    }
}