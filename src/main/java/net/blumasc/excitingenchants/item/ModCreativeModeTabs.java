package net.blumasc.excitingenchants.item;

import net.blumasc.blubasics.block.BaseModBlocks;
import net.blumasc.blubasics.item.BaseModItems;
import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
                        output.accept(ModItems.CLEANSING_SOAP);
                        output.accept(ModBlocks.CURSED_OBSIDIAN);
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
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.BARBED_HOOK, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.CLEAVING, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.CLEAVING, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.CLEAVING, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.BULLET_TIME, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.BULLET_TIME, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.BULLET_TIME, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.BULWARK, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.BULWARK, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.BULWARK, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.BULWARK, 4));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.BULWARK, 5));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.BUOYANT, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.CLOUD_STEP, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.CLOUD_STEP, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.COLLECTING_POCKETS, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.COLLECTING_POCKETS, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.COLLECTING_POCKETS, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.CONTINGENCY, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DASH, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DASH, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DASH, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DEQUIPING, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DEQUIPING, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DEQUIPING, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DEVILS_TOOL, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DEVOURING, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DEVOURING, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DEVOURING, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DEVOURING, 4));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DEVOURING, 5));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DIVINER, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DIVINER, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DIVINER, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DIVINER, 4));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.DIVINER, 5));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.EARTHEN_SPIKE, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.EARTHEN_SPIKE, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.EARTHEN_SPIKE, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.ECHOING, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.ENDER_POCKETS, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.ENDER_POCKETS, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.ENDER_POCKETS, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.FORK, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.FRENZY, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.FRENZY, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.FRENZY, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GALVANIZING, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GALVANIZING, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GALVANIZING, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GALVANIZING, 4));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GALVANIZING, 5));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GRAPE_SHOT, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GRAPE_SHOT, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GRAPE_SHOT, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GRAVITY_IMPLOSION, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GRAVITY_IMPLOSION, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GRAVITY_IMPLOSION, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GRAVITY_IMPLOSION, 4));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GRAVITY_IMPLOSION, 5));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.HUNGRY, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.IMMORTALITY, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.LIVING_WOOD, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.MAGNETIC, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.PICKERANG, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.PICKERANG, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.PICKERANG, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.PICKERANG, 4));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.PICKERANG, 5));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.PINNING, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.PINNING, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.PINNING, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.POCKET_SAND, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.POCKET_SAND, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.POCKET_SAND, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.REAPING, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.REAPING, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.REAPING, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.REVERBERATING, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.REVERBERATING, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.REVERBERATING, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.REVERBERATING, 4));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.REVERBERATING, 5));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.ROPED, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.ROPED, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.ROPED, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.ROPED, 4));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SANGUINE, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SCORCH, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SCULKING, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SCULKING, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SCULKING, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SHARING, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SHARING, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SHARING, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SHARING, 4));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SHARING, 5));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SOUL_RETRIEVER, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SPIKED_STINGER, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SPIKED_STINGER, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SPIKED_STINGER, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SPLATTER, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SPLATTER, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SPLATTER, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SPLATTER, 4));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SPLATTER, 5));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SQUID_ENCOUNTER, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SQUID_ENCOUNTER, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SQUID_ENCOUNTER, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SQUID_ENCOUNTER, 4));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SQUID_ENCOUNTER, 5));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.TIME_REMNANT, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.TIME_REMNANT, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.TIME_REMNANT, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.TRAILING, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.TRAILING, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.TRAILING, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.TRAP_DIGGER, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.TRAP_DIGGER, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.TRAP_DIGGER, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.VOLLEY, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.VOLLEY, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.VOLLEY, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.VOLLEY, 4));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.YOINKING, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.YOINKING, 2));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.YOINKING, 3));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.BAD_BAIT, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.BLACK_THUMB, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.CHORUS, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.FLASH_FREEZE, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.GHOULS, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.HELIUM, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.HIM, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.MIDAS, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.NEEDED_STABILITY, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.NERF, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.RUBBER_BAND, 1));
                        output.accept(enchantedBook(itemDisplayParameters, ModEnchantments.SPARKING, 1));
                    }).build());

    public static void register(IEventBus eventBus)
    {
        CREATIVE_MODE_TAB.register(eventBus);
    }

    private static ItemStack enchantedBook(CreativeModeTab.ItemDisplayParameters params,
                                           ResourceKey<Enchantment> enchantmentKey,
                                           int level) {
        ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);

        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            var lookup = params.holders()
                    .lookup(Registries.ENCHANTMENT)
                    .orElseThrow();

            var enchantment = lookup.getOrThrow(enchantmentKey);

            mutable.set(enchantment, level);
        });


        return stack;
    }
}
