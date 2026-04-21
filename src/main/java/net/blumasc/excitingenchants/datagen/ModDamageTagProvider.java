package net.blumasc.excitingenchants.datagen;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.damage.ModDamageTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.DamageTypeTagsProvider;
import net.minecraft.tags.DamageTypeTags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModDamageTagProvider extends DamageTypeTagsProvider {
    public ModDamageTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, ExcitingEnchantsMod.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(DamageTypeTags.BYPASSES_SHIELD)
                .add(ModDamageTypes.SANGUINE_DAMAGE)
                .add(ModDamageTypes.SMOKE_DAMAGE);
        tag(DamageTypeTags.BYPASSES_ARMOR)
                .add(ModDamageTypes.SANGUINE_DAMAGE);
        tag(DamageTypeTags.NO_KNOCKBACK)
                .add(ModDamageTypes.SANGUINE_DAMAGE)
                .add(ModDamageTypes.SMOKE_DAMAGE);
        tag(DamageTypeTags.BYPASSES_COOLDOWN)
                .add(ModDamageTypes.SANGUINE_DAMAGE)
                .add(ModDamageTypes.SMOKE_DAMAGE);
        tag(DamageTypeTags.BYPASSES_INVULNERABILITY)
                .add(ModDamageTypes.SANGUINE_DAMAGE);
    }
}
