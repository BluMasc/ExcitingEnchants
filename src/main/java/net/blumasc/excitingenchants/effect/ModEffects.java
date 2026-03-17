package net.blumasc.excitingenchants.effect;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.effect.custom.CastleModeEffect;
import net.blumasc.excitingenchants.effect.custom.MeatCrazedEffect;
import net.blumasc.excitingenchants.effect.custom.PinnedEffect;
import net.blumasc.excitingenchants.effect.custom.RainbowEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS = DeferredRegister.create(
            BuiltInRegistries.MOB_EFFECT, ExcitingEnchantsMod.MODID
    );
    public static final Holder<MobEffect> PINNED = MOB_EFFECTS.register("pinned",
            () -> new PinnedEffect()
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "slowness_pinned"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.JUMP_STRENGTH, ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "weak_knees_pinned"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));

    public static final Holder<MobEffect> INKED = MOB_EFFECTS.register("inked",
            () -> new PinnedEffect()
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "slowness_inked"), -0.05, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
    );

    public static final Holder<MobEffect> RAINBOW = MOB_EFFECTS.register("rainbow",
            () -> new RainbowEffect()
    );

    public static final Holder<MobEffect> MEAT_CRAZED = MOB_EFFECTS.register("meat_crazed",
            () -> new MeatCrazedEffect()
    );

    public static final Holder<MobEffect> CASTLE_MODE = MOB_EFFECTS.register("castle_mode",
            () -> new CastleModeEffect()
                    .addAttributeModifier(Attributes.MOVEMENT_SPEED, ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "slowness_castle"), -1.0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL)
                    .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE, ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, "knockback_castle"), 1.0, AttributeModifier.Operation.ADD_VALUE)
    );


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
    }
}
