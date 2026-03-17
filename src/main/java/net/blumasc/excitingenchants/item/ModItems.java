package net.blumasc.excitingenchants.item;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.item.custom.BidentItem;
import net.blumasc.excitingenchants.item.custom.BloodOrbItem;
import net.blumasc.excitingenchants.item.custom.GoldEatenItem;
import net.blumasc.excitingenchants.state.PlayerEnchantmentStateHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(ExcitingEnchantsMod.MODID);

    public static final DeferredItem<Item> BLOOD_ORB = ITEMS.register("blood_orb",
            () -> new BloodOrbItem(new Item.Properties()));
    public static final DeferredItem<Item> SWORDSPEAR_3D = ITEMS.register("swordspear_3d",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GASSTATION_SUSHI = ITEMS.register("box_of_gasstation_sushi",
            () -> new Item(new Item.Properties().food(ModFoodProperties.GASSTATION_SUSHI)));

    public static final DeferredItem<Item> RAW_CARP = ITEMS.register("carp",
            () -> new Item(new Item.Properties().food(ModFoodProperties.RAW_CARP)));

    public static final DeferredItem<Item> COOKED_CARP = ITEMS.register("cooked_carp",
            () -> new Item(new Item.Properties().food(ModFoodProperties.COOKED_CARP)));

    public static final DeferredItem<Item> RAW_RAINBOW_FISH = ITEMS.register("parrotfish",
            () -> new Item(new Item.Properties().food(ModFoodProperties.RAW_RAINBOW_FISH)));

    public static final DeferredItem<Item> COOKED_RAINBOW_FISH = ITEMS.register("cooked_parrotfish",
            () -> new Item(new Item.Properties().food(ModFoodProperties.COOKED_RAINBOW_FISH)));

    public static final DeferredItem<Item> RARE_FISH = ITEMS.register("rare_fish",
            () -> new Item(new Item.Properties()));

    public static final DeferredItem<Item> GOLDEN_MEAT = ITEMS.register("golden_meat",
            () -> new GoldEatenItem(new Item.Properties().food(ModFoodProperties.GOLDEN_MEAT)));

    public static final DeferredItem<Item> COOKED_GOLDEN_MEAT = ITEMS.register("cooked_golden_meat",
            () -> new GoldEatenItem(new Item.Properties().food(ModFoodProperties.COOKED_GOLDEN_MEAT)));

    public static final DeferredItem<Item> COOKED_AUTHENTIC_MEAT = ITEMS.register("cooked_real_authentic_meat",
            () -> new Item(new Item.Properties().food(ModFoodProperties.SUSPICIOUS_MEAT)));

    public static final DeferredItem<Item> AUTHENTIC_MEAT = ITEMS.register("real_authentic_meat",
            () -> new Item(new Item.Properties().food(ModFoodProperties.COOKED_SUSPICIOUS_MEAT)));

    public static final DeferredItem<Item> HOTDOG = ITEMS.register("hot_dog",
            () -> new Item(new Item.Properties().food(ModFoodProperties.HOT_DOG)){
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
                    tooltipComponents.add(Component.translatable(stack.getDescriptionId()+".tooltip").withStyle(ChatFormatting.GRAY));
                    super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
                }
            });

    public static final DeferredItem<Item> OVERGROWN_SPEAR = ITEMS.register("overgrown_swordspear",
            () -> new Item(new Item.Properties().stacksTo(1)){
                @Override
                public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
                    if(usedHand != InteractionHand.MAIN_HAND) return super.use(level, player, usedHand);
                    if(!(player.getOffhandItem().getItem() instanceof ShearsItem)) return super.use(level, player, usedHand);
                    ItemStack shears = player.getOffhandItem();
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SHEEP_SHEAR, SoundSource.PLAYERS);
                    ItemStack kelp = new ItemStack(Items.KELP, player.getRandom().nextInt(3) + 1);
                    shears.hurtAndBreak(1, player, LivingEntity.getSlotForHand(InteractionHand.OFF_HAND));
                    if (!player.addItem(kelp)) {
                        player.spawnAtLocation(kelp);
                    }
                    player.setItemInHand(usedHand, RUSTED_SPEAR.toStack());
                    return InteractionResultHolder.consume(player.getMainHandItem());
                }
            });

    public static final DeferredItem<Item> RUSTED_SPEAR = ITEMS.register("rusted_swordspear",
            () -> new Item(new Item.Properties().stacksTo(1)){
                @Override
                public InteractionResult useOn(UseOnContext context) {
                    BlockPos pos = context.getClickedPos();
                    Level l = context.getLevel();
                    if(context.getPlayer() == null) return super.useOn(context);
                    if(l.getBlockState(pos).is(Blocks.GRINDSTONE)){
                        l.playSound(null, pos, SoundEvents.GRINDSTONE_USE, SoundSource.PLAYERS);
                        if(l.random.nextInt(10)==0){
                            Player p = context.getPlayer();
                            p.setItemInHand(context.getHand(), new ItemStack(DULL_SPEAR.get()));
                            return InteractionResult.sidedSuccess(l.isClientSide());
                        }
                    }
                    return super.useOn(context);
                }
            });

    public static final DeferredItem<Item> DULL_SPEAR = ITEMS.register("dull_swordspear",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> SUPERHEATED_SPEAR = ITEMS.register("superheated_swordspear",
            () -> new Item(new Item.Properties().stacksTo(1)) {
                @Override
                public InteractionResult useOn(UseOnContext context) {
                    BlockPos pos = context.getClickedPos();
                    Level level = context.getLevel();
                    BlockState state = level.getBlockState(pos);

                    if (state.is(Blocks.WATER_CAULDRON)) {
                        int waterLevel = state.getValue(LayeredCauldronBlock.LEVEL);

                        if (!level.isClientSide()) {
                            ItemStack stack = context.getItemInHand();
                            stack.shrink(1);
                            ItemStack newItem = new ItemStack(MELLEABLE_SPEAR.get());
                            Player player = context.getPlayer();
                            if (player != null) {
                                player.getInventory().add(newItem);
                            }
                            if (waterLevel == 1) {
                                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
                            } else {
                                level.setBlock(pos, state.setValue(LayeredCauldronBlock.LEVEL, waterLevel - 1), 3);
                            }
                            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.0f);
                            if (level instanceof ServerLevel serverLevel) {
                                serverLevel.sendParticles(
                                        ParticleTypes.CLOUD,
                                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                                        12, 0.3, 0.1, 0.3, 0.02
                                );
                            }
                        }

                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }

                    return super.useOn(context);
                }

                @Override
                public void inventoryTick(ItemStack stack, Level level, Entity entity, int slotId, boolean isSelected) {
                    entity.setRemainingFireTicks(30);
                    super.inventoryTick(stack, level, entity, slotId, isSelected);
                }
            });

    public static final DeferredItem<Item> MELLEABLE_SPEAR = ITEMS.register("melleable_swordspear",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<Item> REPAIRED_SPEAR = ITEMS.register("repaired_swordspear",
            () -> new Item(new Item.Properties().stacksTo(1)){
                @Override
                public InteractionResult useOn(UseOnContext context) {
                    BlockPos pos = context.getClickedPos();
                    Level level = context.getLevel();
                    BlockState state = level.getBlockState(pos);

                    if (state.is(Blocks.POWDER_SNOW_CAULDRON)) {
                        if (!level.isClientSide()) {
                            ItemStack stack = context.getItemInHand();
                            stack.shrink(1);
                            ItemStack newItem = new ItemStack(SPEAR_BLANK.get());
                            Player player = context.getPlayer();
                            if (player != null) {
                                player.getInventory().add(newItem);
                            }
                            level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
                            level.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.BLOCKS, 0.5f, 2.0f);
                            if (level instanceof ServerLevel serverLevel) {
                                serverLevel.sendParticles(
                                        ParticleTypes.CLOUD,
                                        pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5,
                                        12, 0.3, 0.1, 0.3, 0.02
                                );
                            }
                        }

                        return InteractionResult.sidedSuccess(level.isClientSide());
                    }

                    return super.useOn(context);
                }
            });
    public static final DeferredItem<Item> SPEAR_BLANK = ITEMS.register("swordspear_blank",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final DeferredItem<SwordItem> SWORDSPEAR = ITEMS.register("swordspear",
            () -> new SwordItem(Tiers.DIAMOND, (new Item.Properties().attributes(SwordItem.createAttributes(Tiers.DIAMOND, 3, -2.4F)))));

    public static final DeferredItem<BidentItem> BIDENT = ITEMS.register("bident",
            () -> new BidentItem(new Item.Properties().durability(32)));

    public static final DeferredItem<Item> BIDENT_3D = ITEMS.register("bident_3d",
            () -> new Item(new Item.Properties()));


    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
