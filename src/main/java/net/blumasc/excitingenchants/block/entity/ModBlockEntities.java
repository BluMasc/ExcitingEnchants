package net.blumasc.excitingenchants.block.entity;

import net.blumasc.excitingenchants.ExcitingEnchantsMod;
import net.blumasc.excitingenchants.block.ModBlocks;
import net.blumasc.excitingenchants.block.entity.custom.BreakingBlockEntity;
import net.blumasc.excitingenchants.block.entity.custom.CurseTableBlockEntity;
import net.blumasc.excitingenchants.block.entity.custom.TrapBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, ExcitingEnchantsMod.MODID);


    public static final Supplier<BlockEntityType<TrapBlockEntity>> PITFALL_TRAP_BE=
            BLOCK_ENTITIES.register("pitfall_trap_be", () -> BlockEntityType.Builder.of(
                    TrapBlockEntity::new, ModBlocks.PITFALL_TRAP.get()
            ).build(null));

    public static final Supplier<BlockEntityType<CurseTableBlockEntity>> CURSE_TABLE_BE=
            BLOCK_ENTITIES.register("curse_table_be", () -> BlockEntityType.Builder.of(
                    CurseTableBlockEntity::new, ModBlocks.CURSE_TABLE.get()
            ).build(null));

    public static final Supplier<BlockEntityType<BreakingBlockEntity>> BREAKING_BLOCK_BE=
            BLOCK_ENTITIES.register("breaking_block_be", () -> BlockEntityType.Builder.of(
                    BreakingBlockEntity::new, ModBlocks.BREAKING_BLOCK.get()
            ).build(null));



    public static void register(IEventBus bus){
        BLOCK_ENTITIES.register(bus);
    }
}
