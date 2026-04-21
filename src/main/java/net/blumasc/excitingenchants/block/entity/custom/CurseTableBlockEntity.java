package net.blumasc.excitingenchants.block.entity.custom;

import net.blumasc.blubasics.item.BaseModItems;
import net.blumasc.blubasics.sound.BaseModSounds;
import net.blumasc.excitingenchants.block.entity.ModBlockEntities;
import net.blumasc.excitingenchants.enchantment.ModEnchantments;
import net.blumasc.excitingenchants.item.ModItems;
import net.blumasc.excitingenchants.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CurseTableBlockEntity extends BlockEntity {

    public final ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected int getStackLimit(int slot, ItemStack stack) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (!level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private float rotation;
    private int progress = 0;
    private int maxProgress = 72;
    private boolean succesfullCraft = false;
    public static Item catalyst = BaseModItems.BEETLE_HORN.get();
    public static Item strongCatalyst = BaseModItems.SUN_HORN.get();

    public CurseTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.CURSE_TABLE_BE.get(), pos, blockState);
    }

    public float getRenderingRotation() {
        rotation += 0.5f;
        if (rotation >= 360) {
            rotation = 0;
        }
        return rotation;
    }

    public boolean isCatalyst(ItemStack stack) {
        return stack.is(catalyst) || stack.is(strongCatalyst);
    }

    public boolean isStrongCatalyst(ItemStack stack) {
        return stack.is(strongCatalyst);
    }

    private boolean hasFourCatalysts() {
        for (int i = 1; i <= 4; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty() || !isCatalyst(stack)) return false;
        }
        return true;
    }

    private boolean allStrongCatalysts() {
        for (int i = 1; i <= 4; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (stack.isEmpty() || !isStrongCatalyst(stack)) return false;
        }
        return true;
    }

    private List<Holder<Enchantment>> getApplicableCurses(ItemStack stack) {
        List<Holder<Enchantment>> curses = new ArrayList<>();
        if (level == null) return curses;

        var registry = level.registryAccess().registryOrThrow(Registries.ENCHANTMENT);
        for (Holder<Enchantment> holder : registry.holders().toList()) {
            Enchantment enchantment = holder.value();
            if (!holder.is(EnchantmentTags.CURSE)) continue;
            if (!canEnchant(stack, holder)) continue;
            if (EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet().stream().anyMatch(h -> h.equals(holder))) continue;
            curses.add(holder);
        }
        return curses;
    }
    private List<Holder<Enchantment>> getNearbyItemCurses(ItemStack mainItem) {
        List<Holder<Enchantment>> found = new ArrayList<>();
        if (level == null) return found;

        AABB searchBox = new AABB(worldPosition).inflate(3.0);
        List<ItemEntity> nearbyEntities = level.getEntitiesOfClass(ItemEntity.class, searchBox);

        for (ItemEntity itemEntity : nearbyEntities) {
            ItemStack groundStack = itemEntity.getItem();
            if (groundStack.isEmpty()) continue;
            var enchantments = EnchantmentHelper.getEnchantmentsForCrafting(groundStack);
            for (Map.Entry<Holder<Enchantment>, Integer> entry : enchantments.entrySet()) {
                Holder<Enchantment> holder = entry.getKey();
                if (!holder.is(EnchantmentTags.CURSE)) continue;
                if (!canEnchant(mainItem, holder)) continue;
                if (EnchantmentHelper.getEnchantmentsForCrafting(mainItem).keySet().stream().anyMatch(h -> h.equals(holder))) continue;
                if (!found.contains(holder)) {
                    found.add(holder);
                }
            }
        }
        return found;
    }

    private boolean canEnchant(ItemStack item, Holder<Enchantment> enchantment){
        if(item.getEnchantmentLevel(enchantment)>0)return false;
        if(item.is(Items.BOOK)) return true;
        if(item.getItem() instanceof BlockItem && enchantment.is(ModEnchantments.NEEDED_STABILITY.location()))return true;
        if(enchantment.is(ModEnchantments.CHORUS.location())) return true;
        if(enchantment.is(ModEnchantments.HELIUM.location()))return true;
        return enchantment.value().canEnchant(item);
    }

    private void applyRandomCurse(List<Holder<Enchantment>> curses) {
        if (curses.isEmpty()) return;
        RandomSource random = level.getRandom();
        Holder<Enchantment> chosen = curses.get(random.nextInt(curses.size()));

        ItemStack mainItem = inventory.getStackInSlot(0);
        ItemStack result = mainItem.copy();
        if(result.is(Items.BOOK)){
            result = Items.ENCHANTED_BOOK.asItem().getDefaultInstance().copy();
        }
        EnchantmentHelper.updateEnchantments(result, mutable -> mutable.set(chosen, 1));
        inventory.setStackInSlot(0, result);
    }

    private void popOutMainItem() {
        ItemStack mainItem = inventory.getStackInSlot(0);
        if (!mainItem.isEmpty() && level != null) {
            double x = worldPosition.getX() + 0.5;
            double y = worldPosition.getY() + 1.1;
            double z = worldPosition.getZ() + 0.5;
            ItemEntity itemEntity = new ItemEntity(level, x, y, z, mainItem.copy());
            itemEntity.setDefaultPickUpDelay();
            level.addFreshEntity(itemEntity);
            inventory.setStackInSlot(0, ItemStack.EMPTY);
        }
    }

    public void clearContents() {
        inventory.setStackInSlot(0, ItemStack.EMPTY);
        inventory.setStackInSlot(1, ItemStack.EMPTY);
        inventory.setStackInSlot(2, ItemStack.EMPTY);
        inventory.setStackInSlot(3, ItemStack.EMPTY);
        inventory.setStackInSlot(4, ItemStack.EMPTY);
    }

    public void drops() {
        SimpleContainer inv = new SimpleContainer(inventory.getSlots());
        for (int i = 0; i < inventory.getSlots(); i++) {
            inv.setItem(i, inventory.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inv);
    }

    @Override
    public @Nullable Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("inventory", inventory.serializeNBT(registries));
        tag.putInt("altar.progress", progress);
        tag.putInt("altar.max_progress", maxProgress);
        tag.putBoolean("altar.just_crafted", succesfullCraft);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        inventory.deserializeNBT(registries, tag.getCompound("inventory"));
        progress = tag.getInt("altar.progress");
        maxProgress = tag.getInt("altar.max_progress");
        succesfullCraft = tag.getBoolean("altar.just_crafted");
    }

    public void tick(Level level, BlockPos blockPos, BlockState blockState) {
        if (level.isClientSide) {
            if (succesfullCraft) {
                generateSuccessParticles(level, blockPos);
            }
            if (progress >= 1) {
                generateCraftingParticles(level, blockPos);
            }
        } else {
            if (succesfullCraft) {
                succesfullCraft = false;
                setChanged();
                level.sendBlockUpdated(blockPos, blockState, blockState, 3);
            }
            if(getApplicableCurses(inventory.getStackInSlot(0)).isEmpty()){
                popOutMainItem();
                resetProgress();
                return;
            }

            if (hasCursableItemAndCatalysts()) {
                increaseCraftingProgress();
                setChanged(level, blockPos, blockState);
                level.sendBlockUpdated(blockPos, blockState, blockState, 3);

                if (hasCraftingFinished()) {
                    finishCraft(level, blockPos);
                    resetProgress();
                    level.playSound(null, blockPos, ModSounds.CURSING.get(), SoundSource.BLOCKS, 1f, 1f);
                    succesfullCraft = true;
                    setChanged();
                    level.sendBlockUpdated(blockPos, blockState, blockState, 3);
                }
            } else {
                resetProgress();
                setChanged(level, blockPos, blockState);
                level.sendBlockUpdated(blockPos, blockState, blockState, 3);
            }
        }
    }

    private void finishCraft(Level level, BlockPos blockPos) {
        ItemStack mainItem = inventory.getStackInSlot(0);
        if (mainItem.isEmpty()) return;

        boolean strongMode = allStrongCatalysts();

        List<Holder<Enchantment>> applicableCurses;

        if (strongMode) {
            applicableCurses = getNearbyItemCurses(mainItem);

            if (applicableCurses.isEmpty()) {
                resetProgress();
                return;
            }
        } else {
            applicableCurses = getApplicableCurses(mainItem);
        }
        applyRandomCurse(applicableCurses);
        consumeCatalysts();
    }
    private void consumeCatalysts() {
        for (int i = 1; i <= 4; i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                stack.shrink(1);
                inventory.setStackInSlot(i, stack);
            }
        }
    }

    private void generateSuccessParticles(Level level, BlockPos blockPos) {
        RandomSource random = level.getRandom();
        double cx = blockPos.getX() + 0.5;
        double cy = blockPos.getY() + 1.1;
        double cz = blockPos.getZ() + 0.5;

        for (int i = 0; i < 30; i++) {
            double angle = random.nextDouble() * Math.PI * 2;
            double radius = 0.2 + random.nextDouble() * 0.4;
            double x = cx + Math.cos(angle) * radius;
            double y = cy + random.nextDouble() * 0.2;
            double z = cz + Math.sin(angle) * radius;
            double motionX = Math.cos(angle) * 0.01;
            double motionY = 0.05 + random.nextDouble() * 0.03;
            double motionZ = Math.sin(angle) * 0.01;
            spawnParticle(level, x, y, z, motionX, motionY, motionZ);
        }
    }

    private void resetProgress() {
        progress = 0;
        setChanged();
    }

    private boolean hasCraftingFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasCursableItemAndCatalysts() {
        ItemStack mainItem = inventory.getStackInSlot(0);
        if (mainItem.isEmpty()) return false;
        if (!hasFourCatalysts()) return false;

        if (allStrongCatalysts()) {
            return true;
        }
        List<Holder<Enchantment>> curses = getApplicableCurses(mainItem);
        return !curses.isEmpty();
    }

    private void generateCraftingParticles(Level level, BlockPos blockPos) {
        RandomSource random = level.getRandom();
        double cx = blockPos.getX() + 0.5;
        double cy = blockPos.getY() + 1.1;
        double cz = blockPos.getZ() + 0.5;

        double[][] corners = {
                {0.1, 0.1},
                {0.9, 0.1},
                {0.1, 0.9},
                {0.9, 0.9}
        };

        for (int i = 0; i < corners.length; i++) {
            double[] corner = corners[i];
            double sx = blockPos.getX() + corner[0];
            double sy = blockPos.getY() + 1.05;
            double sz = blockPos.getZ() + corner[1];
            double motionX = (cx - sx) * 0.05 + random.nextGaussian() * 0.005;
            double motionY = 0.02 + random.nextGaussian() * 0.005;
            double motionZ = (cz - sz) * 0.05 + random.nextGaussian() * 0.005;
            spawnParticle(level, sx, sy, sz, motionX, motionY, motionZ);
        }
    }

    public void spawnParticle(Level level, double x, double y, double z,
                                     double moveX, double moveY, double moveZ) {
        if(allStrongCatalysts() ){
            if(getNearbyItemCurses(inventory.getStackInSlot(0)).isEmpty()){
                level.addParticle(ParticleTypes.SMOKE, x, y, z, moveX, moveY, moveZ);
                return;
            }
        }
        level.addParticle(ParticleTypes.WITCH, x, y, z, moveX, moveY, moveZ);
    }
}