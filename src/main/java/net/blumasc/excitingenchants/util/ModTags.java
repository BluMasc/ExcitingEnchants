package net.blumasc.excitingenchants.util;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;

public class ModTags {
    public static class Items{
        public static final TagKey<Item> ENCHANTABLE_DROP_EDITOR = createTag("enchantable_drop_editor");
        public static final TagKey<Item> ENCHANTABLE_SHOOTING = createTag("enchantable_shooting");
        public static final TagKey<Item> GHOUL_EATABLE = createTag("ghoul_eatable");

        private static TagKey<Item> createTag(String name){
            return ItemTags.create(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, name));
        }
    }
    public static class Blocks{
        public static final TagKey<Block> REDSTONE_TARGET = createTag("redstone_target");
        public static final TagKey<Block> SMALL_PLANTS = createTag("small_plants");
        private static TagKey<Block> createTag(String name){
            return BlockTags.create(ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, name));
        }
    }
    public static class EntityTypes{

        public static final TagKey<EntityType<?>> SENSITIVE_TO_DEVILS_IMPALING =createTag("sensitive_to_devils_impaling");
        public static final TagKey<EntityType<?>> HUMANOIDS =createTag("humanoids");
        private static TagKey<EntityType<?>> createTag(String name){
            return TagKey.create(
                    Registries.ENTITY_TYPE,
                    ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, name)
            );
        }
    }
    public static class EnchantmentTypes {
        public static final TagKey<Enchantment> HEAD_SENSE =createTag("head_sense");
        public static final TagKey<Enchantment> MIND_POWER =createTag("mind_power");
        public static final TagKey<Enchantment> CHEST_EXCLUSIVE =createTag("chest_exclusive");
        public static final TagKey<Enchantment> POCKETS =createTag("pockets");
        public static final TagKey<Enchantment> LEG_MOVING =createTag("leg_moving");
        public static final TagKey<Enchantment> FEET_EXCLUSIVE =createTag("feet_exclusive");
        public static final TagKey<Enchantment> SMELT_SILK =createTag("smelt_silk");
        public static final TagKey<Enchantment> MOLTEN_MAGNETS =createTag("molten_magnets");
        public static final TagKey<Enchantment> VEIN_MINER_INCOMPATIBLE =createTag("vein_miner_incompatible");
        public static final TagKey<Enchantment> SWORD_MUTUAL_EXCLUSIVE =createTag("sword_mutual_exclusive");
        public static final TagKey<Enchantment> AXE_MUTUAL_EXCLUSIVE =createTag("axe_mutual_exclusive");
        public static final TagKey<Enchantment> HOE_MUTUAL_EXCLUSIVE =createTag("hoe_mutual_exclusive");
        public static final TagKey<Enchantment> SHOVEL_MUTUAL_EXCLUSIVE =createTag("shovel_mutual_exclusive");
        public static final TagKey<Enchantment> PICKERANG_EXCLUSIVE =createTag("pickerang_exclusive");
        public static final TagKey<Enchantment> DEQUIPING_EXCLUSIVE =createTag("dequiping_exclusive");
        public static final TagKey<Enchantment> MACE_LAND_EFFECT =createTag("mace_land_effect");
        public static final TagKey<Enchantment> ARROW_HIT_EFFECT =createTag("arrow_hit_effect" );
        public static final TagKey<Enchantment> ARROW_SHOOT_EFFECT =createTag("arrow_shoot_effect");
        public static final TagKey<Enchantment> CROSSBOW_EXCLUSIVE =createTag("crossbow_exclusive");
        public static final TagKey<Enchantment> FISHING_ROD_HOOK =createTag("fishing_rod_hook");
        public static final TagKey<Enchantment> CHANNELING_EXCLUSIVE =createTag("channeling_exclusive");
        public static final TagKey<Enchantment> FORK_EXCLUSIVE =createTag("fork_exclusive");
        public static final TagKey<Enchantment> SHIELD_EXCLUSIVE = createTag("shield_exclusive") ;

        private static TagKey<Enchantment> createTag(String name){
            return TagKey.create(
                    Registries.ENCHANTMENT,
                    ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, name)
            );
        }
    }
}
