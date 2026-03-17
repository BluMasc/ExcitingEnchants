package net.blumasc.excitingenchants.block.custom;

import com.mojang.serialization.MapCodec;
import net.blumasc.excitingenchants.block.entity.ModBlockEntities;
import net.blumasc.excitingenchants.block.entity.custom.TrapBlockEntity;
import net.blumasc.excitingenchants.effect.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class TrapBlock extends BaseEntityBlock {

    public static final MapCodec<TrapBlock> CODEC = simpleCodec(TrapBlock::new);

    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final VoxelShape COLLISION =
            Block.box(0, 0, 0, 16, 1, 16);

    @Override
    public VoxelShape getCollisionShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return COLLISION;
    }

    @Override
    protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state) {
        return;
    }



    public TrapBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ACTIVE, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ACTIVE);
    }

    @Override
    public void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity
    ) {
        if(!(entity instanceof LivingEntity))return;
        if (!level.isClientSide && state.getValue(ACTIVE)) {

            level.destroyBlock(pos, false);

            if (entity instanceof LivingEntity living) {
                living.teleportTo(pos.getBottomCenter().x, pos.getBottomCenter().y, pos.getBottomCenter().z);
                living.addEffect(new MobEffectInstance(
                        ModEffects.PINNED,
                        15*20,
                        0
                ));
            }
        }

        super.stepOn(level, pos, state, entity);
    }

    public static void validateBelow(Level level, BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);

        boolean isFullBlock = belowState.isSolidRender(level, belowPos);
        boolean needsShovel = belowState.is(BlockTags.MINEABLE_WITH_SHOVEL);

        if (!isFullBlock || !needsShovel) {
            level.destroyBlock(pos, false);
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public VoxelShape getVisualShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return COLLISION;
    }

    @Override
    public VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return COLLISION;
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction direction,
            BlockState neighborState,
            LevelAccessor level,
            BlockPos pos,
            BlockPos neighborPos
    ) {
        if (direction == Direction.DOWN && level instanceof Level lvl && !lvl.isClientSide) {
            validateBelow(lvl, pos);
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new TrapBlockEntity(blockPos, blockState);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type
    ) {
        return level.isClientSide ? null :
                createTickerHelper(type, ModBlockEntities.PITFALL_TRAP_BE.get(), TrapBlockEntity::tick);
    }
}
