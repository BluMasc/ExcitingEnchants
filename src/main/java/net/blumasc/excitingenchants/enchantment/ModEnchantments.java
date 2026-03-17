package net.blumasc.excitingenchants.enchantment;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.holderset.AllBlockItemsHolderSet;
import net.blumasc.excitingenchants.holderset.EverythingItemsHolderSet;
import net.blumasc.excitingenchants.util.ModTags;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.neoforged.neoforge.common.Tags;

public class ModEnchantments {

    public static final ResourceKey<Enchantment> SPLATTER = makeEnchant("splatter");

    public static final ResourceKey<Enchantment> HUNGRY = makeEnchant( "hungry");

    public static final ResourceKey<Enchantment> DIVINER = makeEnchant( "diviner");

    public static final ResourceKey<Enchantment> SCULKING = makeEnchant( "sculking");

    public static final ResourceKey<Enchantment> SQUID_ENCOUNTER = makeEnchant("squid_encounter");

    public static final ResourceKey<Enchantment> BULWARK = makeEnchant( "bulwark");

    public static final ResourceKey<Enchantment> DASH = makeEnchant( "dash");

    public static final ResourceKey<Enchantment> COLLECTING_POCKETS = makeEnchant( "collecting_pockets");

    public static final ResourceKey<Enchantment> BUOYANT = makeEnchant("buoyant");

    public static final ResourceKey<Enchantment> CLOUD_STEP = makeEnchant("cloud_step");

    public static final ResourceKey<Enchantment> REVERBERATING = makeEnchant( "reverberating");

    public static final ResourceKey<Enchantment> SCORCH = makeEnchant("scorch");

    public static final ResourceKey<Enchantment> MAGNETIC = makeEnchant("magnetic");

    public static final ResourceKey<Enchantment> CLEAVING = makeEnchant("blucleave");

    public static final ResourceKey<Enchantment> FRENZY = makeEnchant("frenzy");

    public static final ResourceKey<Enchantment> SOUL_RETRIEVER = makeEnchant("soul_retriever");

    public static final ResourceKey<Enchantment> REAPING = makeEnchant("reaping");

    public static final ResourceKey<Enchantment> POCKET_SAND = makeEnchant("pocket_sand");

    public static final ResourceKey<Enchantment> TRAP_DIGGER = makeEnchant("trap_digger");

    public static final ResourceKey<Enchantment> PICKERANG = makeEnchant("pickerang");

    public static final ResourceKey<Enchantment> EARTHEN_SPIKE = makeEnchant("earthen_spike");

    public static final ResourceKey<Enchantment> DEQUIPING = makeEnchant("dequiping");

    public static final ResourceKey<Enchantment> GRAVITY_IMPLOSION = makeEnchant("gravity_implosion");

    public static final ResourceKey<Enchantment> ROPED = makeEnchant("roped");

    public static final ResourceKey<Enchantment> PINNING = makeEnchant("pinning");

    public static final ResourceKey<Enchantment> TRAILING = makeEnchant("trailing");

    public static final ResourceKey<Enchantment> VOLLEY = makeEnchant("volley");

    public static final ResourceKey<Enchantment> LIVING_WOOD = makeEnchant("living_wood");

    public static final ResourceKey<Enchantment> BULLET_TIME = makeEnchant("bullet_time");

    public static final ResourceKey<Enchantment> ECHOING = makeEnchant("echoing");

    public static final ResourceKey<Enchantment> SHARING = makeEnchant("sharing");

    public static final ResourceKey<Enchantment> GALVANIZING = makeEnchant("galvanizing");

    public static final ResourceKey<Enchantment> SANGUINE = makeEnchant("sanguine");

    public static final ResourceKey<Enchantment> GRAPE_SHOT = makeEnchant("grape_shot");

    public static final ResourceKey<Enchantment> YOINKING = makeEnchant("yoinking");

    public static final ResourceKey<Enchantment> FLASH_FREEZE = makeEnchant("flash_freeze");

    public static final ResourceKey<Enchantment> BARBED_HOOK = makeEnchant("barbed_hook");

    public static final ResourceKey<Enchantment> FORK = makeEnchant("fork");

    public static final ResourceKey<Enchantment> POSEIDON = makeEnchant("poseidon");

    public static final ResourceKey<Enchantment> DEVILS_TOOL = makeEnchant("devils_tool");

    public static final ResourceKey<Enchantment> DEVOURING = makeEnchant("devouring");

    public static final ResourceKey<Enchantment> CONTINGENCY = makeEnchant("contingeny");

    public static final ResourceKey<Enchantment> IMMORTALITY = makeEnchant("immortality");

    public static final ResourceKey<Enchantment> SPIKED_STINGER = makeEnchant("spiked_stinger");

    public static final ResourceKey<Enchantment> HELIUM = makeEnchant("helium");

    public static final ResourceKey<Enchantment> MIDAS = makeEnchant("midas");

    public static final ResourceKey<Enchantment> CHORUS = makeEnchant("chorus");

    public static final ResourceKey<Enchantment> HIM = makeEnchant("him");

    public static final ResourceKey<Enchantment> GHOULS = makeEnchant("ghouls");

    public static final ResourceKey<Enchantment> SPARKING = makeEnchant("sparking");

    public static final ResourceKey<Enchantment> NEEDED_STABILITY = makeEnchant("needed_stability");

    public static final ResourceKey<Enchantment> BLACK_THUMB = makeEnchant("black_thumb");

    public static final ResourceKey<Enchantment> BAD_BAIT = makeEnchant("bad_bait");

    public static final ResourceKey<Enchantment> NERF = makeEnchant("nerf");

    public static final ResourceKey<Enchantment> RUBBER_BAND = makeEnchant("rubber_band");

    public static ResourceKey<Enchantment> makeEnchant(String name){
        return ResourceKey.create(Registries.ENCHANTMENT,
                ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, name));
    }

    public static void bootstrap(BootstrapContext<Enchantment> context) {
        var enchantments = context.lookup(Registries.ENCHANTMENT);
        var items = context.lookup(Registries.ITEM);

        register(context, SPLATTER, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.WEAPON_ENCHANTABLE),
                        items.getOrThrow(ItemTags.SWORD_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.dynamicCost(2, 2),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.SWORD_MUTUAL_EXCLUSIVE)));

        register(context, HUNGRY, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                        items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                        5,
                        1,
                        Enchantment.dynamicCost(5, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HEAD))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.HEAD_SENSE)));

        register(context, DIVINER, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                        items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HEAD))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.HEAD_SENSE)));

        register(context, SCULKING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                        items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HEAD))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.HEAD_SENSE)));

        register(context, SQUID_ENCOUNTER, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                        items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.CHEST))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.CHEST_EXCLUSIVE)));

        register(context, BULWARK, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                        items.getOrThrow(ItemTags.CHEST_ARMOR_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.CHEST))
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "bulwark_knockback"),
                                Attributes.KNOCKBACK_RESISTANCE,
                                LevelBasedValue.perLevel(0.03F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        )
                )
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "bulwark_speed"),
                                Attributes.MOVEMENT_SPEED,
                                LevelBasedValue.perLevel(-0.02F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        )
                ));

        register(context, DASH, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE),
                        items.getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.LEGS))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.LEG_MOVING)));

        register(context, COLLECTING_POCKETS, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE),
                        items.getOrThrow(ItemTags.LEG_ARMOR_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.LEGS))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.POCKETS)));

        register(context, BUOYANT, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                        items.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.FEET))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.FEET_EXCLUSIVE)));

        register(context, CLOUD_STEP, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                        items.getOrThrow(ItemTags.FOOT_ARMOR_ENCHANTABLE),
                        5,
                        2,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.FEET))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.FEET_EXCLUSIVE))
                .withEffect(
                        EnchantmentEffectComponents.ATTRIBUTES,
                        new EnchantmentAttributeEffect(
                                ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "cloud_jump"),
                                Attributes.JUMP_STRENGTH,
                                LevelBasedValue.perLevel(0.5F),
                                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                        )
                ));

        register(context, REVERBERATING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.MINING_ENCHANTABLE),
                        items.getOrThrow(ItemTags.MINING_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.VEIN_MINER_INCOMPATIBLE)));

        register(context, SCORCH, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ModTags.Items.ENCHANTABLE_DROP_EDITOR),
                        items.getOrThrow(ModTags.Items.ENCHANTABLE_DROP_EDITOR),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.SMELT_SILK)));

        register(context, MAGNETIC, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ModTags.Items.ENCHANTABLE_DROP_EDITOR),
                        items.getOrThrow(ModTags.Items.ENCHANTABLE_DROP_EDITOR),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.MOLTEN_MAGNETS)));

        register(context, CLEAVING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.AXES),
                        items.getOrThrow(ItemTags.AXES),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.AXE_MUTUAL_EXCLUSIVE)));

        register(context, FRENZY, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.AXES),
                        items.getOrThrow(ItemTags.AXES),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.AXE_MUTUAL_EXCLUSIVE)));

        register(context, SOUL_RETRIEVER, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.AXES),
                        items.getOrThrow(ItemTags.AXES),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.AXE_MUTUAL_EXCLUSIVE)));

        register(context, REAPING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.HOES),
                        items.getOrThrow(ItemTags.HOES),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.HOE_MUTUAL_EXCLUSIVE)));

        register(context, POCKET_SAND, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.SHOVELS),
                        items.getOrThrow(ItemTags.SHOVELS),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.SHOVEL_MUTUAL_EXCLUSIVE)));

        register(context, TRAP_DIGGER, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.SHOVELS),
                        items.getOrThrow(ItemTags.SHOVELS),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.SHOVEL_MUTUAL_EXCLUSIVE)));

        register(context, PICKERANG, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.PICKAXES),
                        items.getOrThrow(ItemTags.PICKAXES),
                        5,
                        5,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.PICKERANG_EXCLUSIVE)));

        register(context, EARTHEN_SPIKE, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.PICKAXES),
                        items.getOrThrow(ItemTags.PICKAXES),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND)));

        register(context, DEQUIPING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.MACE_ENCHANTABLE),
                        items.getOrThrow(ItemTags.MACE_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.DEQUIPING_EXCLUSIVE)));

        register(context, GRAVITY_IMPLOSION, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.MACE_ENCHANTABLE),
                        items.getOrThrow(ItemTags.MACE_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.MACE_LAND_EFFECT)));

        register(context, ROPED, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        5,
                        4,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.ARROW_HIT_EFFECT)));

        register(context, PINNING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.ARROW_HIT_EFFECT)));

        register(context, TRAILING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.ARROW_SHOOT_EFFECT)));

        register(context, VOLLEY, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        5,
                        4,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.ARROW_SHOOT_EFFECT)));

        register(context, LIVING_WOOD, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND)));

        register(context, BULLET_TIME, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        items.getOrThrow(ItemTags.BOW_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.ARROW_SHOOT_EFFECT)));

        register(context, ECHOING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        items.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.CROSSBOW_EXCLUSIVE)));

        register(context, SHARING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        items.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.CROSSBOW_EXCLUSIVE)));

        register(context, GALVANIZING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        items.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        5,
                        5,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.CROSSBOW_EXCLUSIVE)));

        register(context, SANGUINE, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        items.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.CROSSBOW_EXCLUSIVE)));

        register(context, GRAPE_SHOT, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        items.getOrThrow(ItemTags.CROSSBOW_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.CROSSBOW_EXCLUSIVE)));

        register(context, YOINKING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
                        items.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.FISHING_ROD_HOOK)));

        register(context, FLASH_FREEZE, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
                        items.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.FISHING_ROD_HOOK)));

        register(context, BARBED_HOOK, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
                        items.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.FISHING_ROD_HOOK)));

        register(context, FORK, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                        items.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.FORK_EXCLUSIVE)));

        register(context, POSEIDON, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                        items.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.CHANNELING_EXCLUSIVE)));

        register(context, DEVILS_TOOL, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                        items.getOrThrow(ItemTags.TRIDENT_ENCHANTABLE),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.CHANNELING_EXCLUSIVE)));

        register(context, DEVOURING, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(Tags.Items.TOOLS_SHIELD),
                        items.getOrThrow(Tags.Items.TOOLS_SHIELD),
                        5,
                        5,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.SHIELD_EXCLUSIVE)));

        register(context, CONTINGENCY, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(Tags.Items.TOOLS_SHIELD),
                        items.getOrThrow(Tags.Items.TOOLS_SHIELD),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.SHIELD_EXCLUSIVE)));

        register(context, IMMORTALITY, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(Tags.Items.TOOLS_SHIELD),
                        items.getOrThrow(Tags.Items.TOOLS_SHIELD),
                        5,
                        1,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND))
                .exclusiveWith(enchantments.getOrThrow(ModTags.EnchantmentTypes.SHIELD_EXCLUSIVE)));

        register(context, SPIKED_STINGER, Enchantment.enchantment(Enchantment.definition(
                        items.getOrThrow(ItemTags.create(ResourceLocation.fromNamespaceAndPath("minecraft","spears"))),
                        items.getOrThrow(ItemTags.create(ResourceLocation.fromNamespaceAndPath("minecraft","spears"))),
                        5,
                        3,
                        Enchantment.dynamicCost(7, 3),
                        Enchantment.dynamicCost(25, 7),
                        2,
                        EquipmentSlotGroup.HAND)));

        register(context, HELIUM, Enchantment.enchantment(Enchantment.definition(
                new EverythingItemsHolderSet(),
                new EverythingItemsHolderSet(),
                5,
                1,
                Enchantment.dynamicCost(7, 3),
                Enchantment.dynamicCost(25, 7),
                2,
                EquipmentSlotGroup.ANY)));

        register(context, MIDAS, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ModTags.Items.ENCHANTABLE_DROP_EDITOR),
                5,
                1,
                Enchantment.dynamicCost(7, 3),
                Enchantment.dynamicCost(25, 7),
                2,
                EquipmentSlotGroup.HAND)));

        register(context, CHORUS, Enchantment.enchantment(Enchantment.definition(
                new EverythingItemsHolderSet(),
                new EverythingItemsHolderSet(),
                5,
                1,
                Enchantment.dynamicCost(7, 3),
                Enchantment.dynamicCost(25, 7),
                2,
                EquipmentSlotGroup.ANY)));

        register(context, HIM, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                5,
                1,
                Enchantment.dynamicCost(7, 3),
                Enchantment.dynamicCost(25, 7),
                2,
                EquipmentSlotGroup.ARMOR)));

        register(context, GHOULS, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                items.getOrThrow(ItemTags.HEAD_ARMOR_ENCHANTABLE),
                5,
                1,
                Enchantment.dynamicCost(7, 3),
                Enchantment.dynamicCost(25, 7),
                2,
                EquipmentSlotGroup.HEAD)));

        register(context, SPARKING, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                5,
                1,
                Enchantment.dynamicCost(7, 3),
                Enchantment.dynamicCost(25, 7),
                2,
                EquipmentSlotGroup.ARMOR)));

        register(context, NEEDED_STABILITY, Enchantment.enchantment(Enchantment.definition(
                new AllBlockItemsHolderSet(),
                new AllBlockItemsHolderSet(),
                5,
                1,
                Enchantment.dynamicCost(7, 3),
                Enchantment.dynamicCost(25, 7),
                2,
                EquipmentSlotGroup.ANY)));

        register(context, BLACK_THUMB, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                items.getOrThrow(ItemTags.ARMOR_ENCHANTABLE),
                5,
                1,
                Enchantment.dynamicCost(7, 3),
                Enchantment.dynamicCost(25, 7),
                2,
                EquipmentSlotGroup.ARMOR)));

        register(context, BAD_BAIT, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
                items.getOrThrow(ItemTags.FISHING_ENCHANTABLE),
                5,
                1,
                Enchantment.dynamicCost(7, 3),
                Enchantment.dynamicCost(25, 7),
                2,
                EquipmentSlotGroup.HAND)));

        register(context, NERF, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ModTags.Items.ENCHANTABLE_SHOOTING),
                items.getOrThrow(ModTags.Items.ENCHANTABLE_SHOOTING),
                5,
                1,
                Enchantment.dynamicCost(7, 3),
                Enchantment.dynamicCost(25, 7),
                2,
                EquipmentSlotGroup.HAND)));

        register(context, RUBBER_BAND, Enchantment.enchantment(Enchantment.definition(
                items.getOrThrow(ItemTags.MACE_ENCHANTABLE),
                items.getOrThrow(ItemTags.MACE_ENCHANTABLE),
                5,
                1,
                Enchantment.dynamicCost(7, 3),
                Enchantment.dynamicCost(25, 7),
                2,
                EquipmentSlotGroup.HAND)));

    }


    private static Holder<Enchantment> register(BootstrapContext<Enchantment> registry, ResourceKey<Enchantment> key,
                                   Enchantment.Builder builder) {
        return registry.register(key, builder.build(key.location()));
    }
}
