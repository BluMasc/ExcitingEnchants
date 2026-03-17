package net.blumasc.excitingenchants.item;

import net.blumasc.blubasics.block.BaseModBlocks;
import net.blumasc.blubasics.item.BaseModItems;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ExcitingEnchantsMod.MODID);

    public static final Supplier<CreativeModeTab> SELECTIVE_POWERS_TAB = CREATIVE_MODE_TAB.register("exciting_enchantments_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocks.CURSE_TABLE)).title(Component.translatable("itemGroup.excitingenchants"))
                    .displayItems((itemDisplayParameters, output) -> {
                        output.accept(BaseModItems.BEETLE_HORN);
                        output.accept(BaseModItems.SUN_HORN);
                        output.accept(ModItems.BLOOD_ORB);
                        output.accept(ModBlocks.PITFALL_TRAP);
                        output.accept(ModBlocks.CURSE_TABLE);
                        output.accept(ModBlocks.INK_LAYER_BLOCK);
                        output.accept(ModBlocks.CLOUD_BLOCK);
                        output.accept(ModBlocks.GOLDEN_BLOCK);
                        output.accept(ModBlocks.GOLDEN_BRICKS);
                        output.accept(ModBlocks.GOLDEN_BRICK_SLAB);
                        output.accept(ModBlocks.GOLDEN_BRICK_WALL);
                        output.accept(ModBlocks.GOLDEN_BRICK_STAIRS);
                        output.accept(ModBlocks.GOLDEN_LOG);
                        output.accept(ModBlocks.GOLDEN_PLANKS);
                        output.accept(ModBlocks.GOLDEN_PLANK_SLAB);
                        output.accept(ModBlocks.GOLDEN_FENCE);
                        output.accept(ModBlocks.GOLDEN_PLANK_STAIRS);
                        output.accept(ModBlocks.GOLDEN_GRASS);
                        output.accept(ModItems.GOLDEN_MEAT);
                        output.accept(ModItems.COOKED_GOLDEN_MEAT);
                        output.accept(ModItems.OVERGROWN_SPEAR);
                        output.accept(ModItems.RUSTED_SPEAR);
                        output.accept(ModItems.DULL_SPEAR);
                        output.accept(ModItems.SUPERHEATED_SPEAR);
                        output.accept(ModItems.MELLEABLE_SPEAR);
                        output.accept(ModItems.REPAIRED_SPEAR);
                        output.accept(ModItems.SPEAR_BLANK);
                        output.accept(ModItems.SWORDSPEAR);
                        output.accept(ModItems.BIDENT);
                        output.accept(ModItems.GASSTATION_SUSHI);
                        output.accept(ModItems.RAW_CARP);
                        output.accept(ModItems.COOKED_CARP);
                        output.accept(ModItems.RAW_RAINBOW_FISH);
                        output.accept(ModItems.COOKED_RAINBOW_FISH);
                        output.accept(ModItems.RARE_FISH);
                        output.accept(ModItems.AUTHENTIC_MEAT);
                        output.accept(ModItems.COOKED_AUTHENTIC_MEAT);
                        output.accept(ModItems.HOTDOG);
                    }).build());

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
