package rbasamoyai.escalated.walkways;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.escalated.index.EscalatedShapes;

public class WideEscalatorCenterBlock extends NarrowEscalatorBlock {

    public WideEscalatorCenterBlock(Properties properties, NonNullSupplier<WalkwaySet> walkwaySetSupplier) {
        super(properties, walkwaySetSupplier);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        WalkwaySlope slope = state.getValue(SLOPE);
        Direction facing = state.getValue(HORIZONTAL_FACING);

        return switch (slope) {
            case BOTTOM -> EscalatedShapes.WIDE_ESCALATOR_CENTER_BOTTOM.get(facing);
            case TOP -> EscalatedShapes.WIDE_ESCALATOR_CENTER_TOP.get(facing);
            case MIDDLE -> EscalatedShapes.WIDE_ESCALATOR_CENTER_MIDDLE.get(facing);
            default -> EscalatedShapes.WIDE_WALKWAY_CENTER;
        };
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        WalkwaySlope slope = state.getValue(SLOPE);
        if (slope == WalkwaySlope.HORIZONTAL || slope == WalkwaySlope.TERMINAL)
            return super.getCollisionShape(state, blockGetter, pos, context);
        Direction facing = state.getValue(HORIZONTAL_FACING);
        WalkwayBlockEntity be = this.getBlockEntity(blockGetter, pos);
        if (be == null)
            return super.getCollisionShape(state, blockGetter, pos, context);

        float visualOffset = be.getVisualProgress();
        return switch (slope) {
            case BOTTOM -> EscalatedShapes.WIDE_ESCALATOR_CENTER_BOTTOM_STEPPED.getShape(visualOffset, facing);
            case MIDDLE -> EscalatedShapes.WIDE_ESCALATOR_CENTER_MIDDLE_STEPPED.getShape(visualOffset, facing);
            case TOP -> EscalatedShapes.WIDE_ESCALATOR_CENTER_TOP_STEPPED.getShape(visualOffset, facing);
            default -> super.getCollisionShape(state, blockGetter, pos, context);
        };
    }

    @Override
    public BlockState transformFromMerge(Level level, BlockState state, BlockPos pos, boolean left, boolean shaft, boolean remove) {
        WalkwaySlope slope = state.getValue(SLOPE);
        Direction facing = state.getValue(HORIZONTAL_FACING);
        if (remove) {
            return this.getWalkwaySet().getWideSideBlock(level, state, pos)
                    .setValue(HORIZONTAL_FACING, facing)
                    .setValue(WideEscalatorSideBlock.LEFT, left)
                    .setValue(SLOPE, slope);
        } else {
            return state;
        }
    }

    @Override
    public boolean connectedToWalkwayOnSide(Level level, BlockState state, BlockPos pos, Direction face) {
        return face.getAxis().isHorizontal();
    }

}
