package net.blumasc.excitingenchants.datagen;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.util.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModEntityTagProvider extends EntityTypeTagsProvider {
    public ModEntityTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, provider, ExcitingEnchantsMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.EntityTypes.SENSITIVE_TO_DEVILS_IMPALING)
                .add(EntityType.HOGLIN)
                .add(EntityType.ZOGLIN)
                .add(EntityType.PIGLIN)
                .add(EntityType.PIGLIN_BRUTE)
                .add(EntityType.ZOMBIFIED_PIGLIN)
                .add(EntityType.WITHER_SKELETON)
                .add(EntityType.BLAZE)
                .add(EntityType.GHAST)
                .add(EntityType.MAGMA_CUBE)
                .add(EntityType.STRIDER)
                .add(EntityType.WITHER);
        tag(ModTags.EntityTypes.HUMANOIDS)
                .add(EntityType.VILLAGER)
                .add(EntityType.VINDICATOR)
                .add(EntityType.WITCH)
                .add(EntityType.PILLAGER)
                .add(EntityType.ILLUSIONER)
                .add(EntityType.EVOKER)
                .add(EntityType.WANDERING_TRADER);
    }
}
