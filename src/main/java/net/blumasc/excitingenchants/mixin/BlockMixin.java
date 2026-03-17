package net.blumasc.excitingenchants.mixin;

import net.blumasc.excitingenchants.state.RedstoneSignalManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class BlockMixin {
    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    private void injectSignal(BlockState state, BlockGetter level, BlockPos pos,
                              Direction direction, CallbackInfoReturnable<Integer> cir) {
        if (RedstoneSignalManager.poweredPositions.containsKey(pos)) {
            cir.setReturnValue(15);
        }
    }
}