package net.blumasc.excitingenchants.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CloudBlock extends Block {

    public static final VoxelShape OUTLINE = Shapes.block();
    public static final VoxelShape COLLISION = Block.box(0, 8, 0, 16, 16, 16);

    public CloudBlock(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level,
                               BlockPos pos, CollisionContext context) {
        return OUTLINE;
    }
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level,
                                        BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity != null) {
                double blockTopY = pos.getY() + 0.5;
                if (entity.getY() < blockTopY) {
                    return Shapes.empty();
                }
            }
        }
        return COLLISION;
    }
    @Override
    public boolean useShapeForLightOcclusion(BlockState state) {
        return false;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level,
                           BlockPos pos, RandomSource random) {
        level.removeBlock(pos, false);
        level.levelEvent(2001, pos, Block.getId(state));
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return true;
    }
}
