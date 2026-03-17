package net.blumasc.excitingenchants.lootmodifiers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.item.ModItems;
import net.blumasc.excitingenchants.util.ModTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

public class MeatDropModifier extends LootModifier {

    public static final MapCodec<MeatDropModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            codecStart(inst).apply(inst, MeatDropModifier::new));

    public MeatDropModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot,
                                                  LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        if (entity == null) return generatedLoot;

        if (!entity.getType().is(ModTags.EntityTypes.HUMANOIDS)) return generatedLoot;

        Entity killer = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
        if (!(killer instanceof LivingEntity livingKiller)) return generatedLoot;
        ItemStack helmet = livingKiller.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.isEmpty()) return generatedLoot;

        boolean hasEnchant = EnchantmentHelper.getTagEnchantmentLevel(
                context.getLevel().registryAccess()
                        .registryOrThrow(net.minecraft.core.registries.Registries.ENCHANTMENT)
                        .getHolderOrThrow(ModEnchantments.GHOULS),
                helmet
        ) > 0;

        if (!hasEnchant) return generatedLoot;

        boolean onFire = entity.isOnFire();
        Item drop = onFire ? ModItems.COOKED_AUTHENTIC_MEAT.get() : ModItems.AUTHENTIC_MEAT.get();

        generatedLoot.add(new ItemStack(drop, 1));
        return generatedLoot;
    }

    @Override
    public MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}