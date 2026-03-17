package net.blumasc.excitingenchants.lootmodifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.blumasc.excitingenchants.datagen.ModChestLoot;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FishingHook;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BadBaitLootModifier extends LootModifier {

    public static final MapCodec<BadBaitLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).apply(inst, BadBaitLootModifier::new));

    public BadBaitLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        if (!(context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof FishingHook hook)) return generatedLoot;

        Player player = hook.getPlayerOwner();
        if (player == null) return generatedLoot;

        ItemStack rod = getRod(player);
        if (rod.isEmpty()) return generatedLoot;

        ServerLevel serverLevel = (ServerLevel) context.getLevel();
        Holder<Enchantment> badBait = serverLevel.registryAccess()
                .registryOrThrow(Registries.ENCHANTMENT)
                .getHolderOrThrow(ModEnchantments.BAD_BAIT);

        if (rod.getEnchantmentLevel(badBait) <= 0) return generatedLoot;

        float luck = (float) player.getLuck()
                + EnchantmentHelper.getFishingLuckBonus(serverLevel, rod, player);

        LootParams params = new LootParams.Builder(serverLevel)
                .withParameter(LootContextParams.ORIGIN, hook.position())
                .withParameter(LootContextParams.TOOL, rod)
                .withParameter(LootContextParams.THIS_ENTITY, hook)
                .withLuck(luck)
                .create(LootContextParamSets.FISHING);

        ObjectArrayList<ItemStack> newLoot = new ObjectArrayList<>();
        rollFishingNoFish(serverLevel, params, newLoot, context.getRandom(), luck);
        // Special chunk rare fish check
        //SpecialChunkData chunkData = SpecialChunkData.get(serverLevel);
        //if (chunkData.isInitialized()) {
        //    ChunkPos bobberChunk = new ChunkPos(hook.blockPosition());
        //    if (bobberChunk.equals(chunkData.getSpecialChunk())) {
        //        if (serverLevel.getRandom().nextFloat() < 0.01f) {
        //            newLoot.add(new ItemStack(ModItems.RARE_FISH.get()));
        //        }
        //    }
        //}

        return newLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    private static void rollFishingNoFish(ServerLevel level, LootParams params,
                                          List<ItemStack> out, RandomSource random, float luck) {
        LootTable junkTable = level.getServer().reloadableRegistries()
                .getLootTable(BuiltInLootTables.FISHING_JUNK);

        LootTable treasureTable = level.getServer().reloadableRegistries()
                .getLootTable(BuiltInLootTables.FISHING_TREASURE);

        LootTable customTable = level.getServer().reloadableRegistries()
                .getLootTable(ModChestLoot.BAD_FISHING_LOOT_TABLE);

        LootContext context = new LootContext.Builder(params).create(Optional.empty());

        int junkWeight     = Math.max(0, 10 + (int)(-2 * luck));
        int treasureWeight = Math.max(0,  2 + (int)( 2 * luck));
        int customWeight   = Math.max(0, 10 + (int)( 1 * luck));

        record WeightedPool(LootTable lootTable, int weight) {}

        List<WeightedPool> weightedPools = new ArrayList<>();
        weightedPools.add(new WeightedPool(junkTable, junkWeight));
        weightedPools.add(new WeightedPool(treasureTable, treasureWeight));

        int totalWeight = weightedPools.stream().mapToInt(WeightedPool::weight).sum() + customWeight;
        if (totalWeight <= 0) return;


        int roll = random.nextInt(totalWeight);
        int cursor = 0;

        for (WeightedPool entry : weightedPools) {
            cursor += entry.weight();
            if (roll < cursor) {
                entry.lootTable().getRandomItemsRaw(context, out::add);
                return;
            }
        }
        customTable.getRandomItemsRaw(context, out::add);
    }

    private static ItemStack getRod(Player player) {
        if (player.getMainHandItem().is(Items.FISHING_ROD)) return player.getMainHandItem();
        if (player.getOffhandItem().is(Items.FISHING_ROD)) return player.getOffhandItem();
        return ItemStack.EMPTY;
    }
}