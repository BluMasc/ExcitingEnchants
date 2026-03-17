package net.blumasc.excitingenchants.compat;

import net.minecraft.world.item.ItemStack;

public record SpearRecipe(ItemStack input, ItemStack catalyst, ItemStack output) {}