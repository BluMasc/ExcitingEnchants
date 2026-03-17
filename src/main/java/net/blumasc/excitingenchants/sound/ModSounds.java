package net.blumasc.excitingenchants.sound;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENT =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, ExcitingEnchantsMod.MODID);

    public static final Supplier<SoundEvent> ROPE = registerSoundEvent("rope");
    public static final Supplier<SoundEvent> GRAPE_SHOT = registerSoundEvent("grape_shot");
    public static final Supplier<SoundEvent> RUMBLE = registerSoundEvent("rumble");

    private static Supplier<SoundEvent> registerSoundEvent(String name){
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(ExcitingEnchantsMod.MODID, name);
        return SOUND_EVENT.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENT.register(eventBus);
    }
}
