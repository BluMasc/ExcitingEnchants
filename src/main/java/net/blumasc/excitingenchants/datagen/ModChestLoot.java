package net.blumasc.excitingenchants.datagen;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.item.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.function.BiConsumer;


public class ModChestLoot implements LootTableSubProvider {

    public static final ResourceKey<LootTable> BAD_FISHING_LOOT_TABLE = ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "bad_fishing_loot"));




    private final HolderLookup.Provider provider;

    public ModChestLoot(HolderLookup.Provider provider) {
        this.provider = provider;
    }

    private LootItem.Builder<?> soggyNote(String message) {
        return LootItem.lootTableItem(Items.PAPER)
                .setWeight(1)
                .apply(SetNameFunction.setName(
                        Component.translatable("item.excitingenchants.soggy_note").withStyle(ChatFormatting.GRAY),
                        SetNameFunction.Target.ITEM_NAME
                ))
                .apply(SetLoreFunction.setLore()
                        .addLine(Component.translatable(message).withStyle(ChatFormatting.ITALIC, ChatFormatting.WHITE))
                );
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(
                BAD_FISHING_LOOT_TABLE,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(UniformGenerator.between(1.0F, 1.0F))
                                .add(LootItem.lootTableItem(ModItems.RAW_CARP.get())
                                        .setWeight(16)
                                        .apply(EnchantRandomlyFunction.randomApplicableEnchantment(provider) .when(LootItemRandomChanceCondition.randomChance(0.1f))))
                                .add(LootItem.lootTableItem(ModItems.RAW_RAINBOW_FISH.get())
                                        .setWeight(14)
                                        .apply(EnchantRandomlyFunction.randomApplicableEnchantment(provider) .when(LootItemRandomChanceCondition.randomChance(0.1f))))
                                .add(LootItem.lootTableItem(ModItems.GASSTATION_SUSHI.get())
                                        .setWeight(2)
                                        .apply(EnchantRandomlyFunction.randomApplicableEnchantment(provider).when(LootItemRandomChanceCondition.randomChance(0.1f))))
                                .add(LootItem.lootTableItem(ModItems.BIDENT.get())
                                        .setWeight(2)
                                        .apply(EnchantRandomlyFunction.randomApplicableEnchantment(provider).when(LootItemRandomChanceCondition.randomChance(0.5f))))
                                .add(soggyNote("item.excitingenchants.soggy_note.lore_0"))
                                .add(soggyNote("item.excitingenchants.soggy_note.lore_1"))
                                .add(soggyNote("item.excitingenchants.soggy_note.lore_2"))
                                .add(soggyNote("item.excitingenchants.soggy_note.lore_3"))
                                .add(soggyNote("item.excitingenchants.soggy_note.lore_4"))
                                .add(soggyNote("item.excitingenchants.soggy_note.lore_5"))
                                .add(soggyNote("item.excitingenchants.soggy_note.lore_6"))
                                .add(soggyNote("item.excitingenchants.soggy_note.lore_7"))
                                .add(LootItem.lootTableItem(Items.SEAGRASS).setWeight(8))
                                .add(LootItem.lootTableItem(Items.KELP).setWeight(6))
                                .add(LootItem.lootTableItem(Items.DEAD_BUSH).setWeight(4))
                                .add(LootItem.lootTableItem(Items.FLOWER_POT).setWeight(3))
                                .add(LootItem.lootTableItem(ModItems.OVERGROWN_SPEAR.get()).setWeight(2))
                                .add(LootItem.lootTableItem(Items.SLIME_BALL)
                                        .setWeight(7)
                                        .apply(EnchantRandomlyFunction.randomApplicableEnchantment(provider) .when(LootItemRandomChanceCondition.randomChance(0.1f))))
                                .add(LootItem.lootTableItem(Items.DIRT)
                                        .setWeight(6)
                                        .apply(EnchantRandomlyFunction.randomApplicableEnchantment(provider) .when(LootItemRandomChanceCondition.randomChance(0.1f))))
                        )
        );
    }
}
