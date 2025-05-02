package rbasamoyai.escalated.walkways;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.escalated.index.EscalatedShapes;

public class WideEscalatorSideBlock extends NarrowEscalatorBlock {

    public static final BooleanProperty LEFT = WideWalkwaySideBlock.LEFT;
    
    public WideEscalatorSideBlock(Properties properties, NonNullSupplier<WalkwaySet> walkwaySetSupplier) {
        super(properties, walkwaySetSupplier);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LEFT);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        WalkwaySlope slope = state.getValue(SLOPE);
        Direction facing = state.getValue(HORIZONTAL_FACING);

        if (state.getValue(LEFT)) {
            return switch (slope) {
                case BOTTOM -> EscalatedShapes.WIDE_ESCALATOR_SIDE_BOTTOM_LEFT.get(facing);
                case TOP -> EscalatedShapes.WIDE_ESCALATOR_SIDE_TOP_LEFT.get(facing);
                case MIDDLE -> EscalatedShapes.WIDE_ESCALATOR_SIDE_MIDDLE_LEFT.get(facing);
                default -> EscalatedShapes.WIDE_WALKWAY_SIDE_LEFT.get(facing);
            };
        } else {
            return switch (slope) {
                case BOTTOM -> EscalatedShapes.WIDE_ESCALATOR_SIDE_BOTTOM_RIGHT.get(facing);
                case TOP -> EscalatedShapes.WIDE_ESCALATOR_SIDE_TOP_RIGHT.get(facing);
                case MIDDLE -> EscalatedShapes.WIDE_ESCALATOR_SIDE_MIDDLE_RIGHT.get(facing);
                default -> EscalatedShapes.WIDE_WALKWAY_SIDE_RIGHT.get(facing);
            };
        }
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
        if (state.getValue(LEFT)) {
            return switch (slope) {
                case BOTTOM -> EscalatedShapes.WIDE_ESCALATOR_SIDE_BOTTOM_LEFT_STEPPED.getShape(visualOffset, facing);
                case MIDDLE -> EscalatedShapes.WIDE_ESCALATOR_SIDE_MIDDLE_LEFT_STEPPED.getShape(visualOffset, facing);
                case TOP -> EscalatedShapes.WIDE_ESCALATOR_SIDE_TOP_LEFT_STEPPED.getShape(visualOffset, facing);
                default -> super.getCollisionShape(state, blockGetter, pos, context);
            };
        } else {
            return switch (slope) {
                case BOTTOM -> EscalatedShapes.WIDE_ESCALATOR_SIDE_BOTTOM_RIGHT_STEPPED.getShape(visualOffset, facing);
                case MIDDLE -> EscalatedShapes.WIDE_ESCALATOR_SIDE_MIDDLE_RIGHT_STEPPED.getShape(visualOffset, facing);
                case TOP -> EscalatedShapes.WIDE_ESCALATOR_SIDE_TOP_RIGHT_STEPPED.getShape(visualOffset, facing);
                default -> super.getCollisionShape(state, blockGetter, pos, context);
            };
        }
    }

    @Override
    public BlockState transformFromMerge(Level level, BlockState state, BlockPos pos, boolean left, boolean shaft, boolean remove) {
        boolean srcLeft = state.getValue(LEFT);
        Direction facing = state.getValue(HORIZONTAL_FACING);
        if (remove) {
            if (srcLeft == left)
                return state;
            return this.getWalkwaySet().getNarrowBlock(level, state, pos)
                    .setValue(HORIZONTAL_FACING, facing)
                    .setValue(SLOPE, state.getValue(SLOPE));
        } else {
            if (srcLeft != left)
                return state;
            return this.getWalkwaySet().getWideCenterBlock(level, state, pos)
                    .setValue(HORIZONTAL_FACING, facing)
                    .setValue(SLOPE, state.getValue(SLOPE));
        }
    }

    @Override
    public boolean connectedToWalkwayOnSide(Level level, BlockState state, BlockPos pos, Direction face) {
        Direction facing = state.getValue(HORIZONTAL_FACING);
        if (face.getAxis() == facing.getAxis())
            return true;
        Direction openFace = state.getValue(LEFT) ? facing.getClockWise() : facing.getCounterClockWise();
        return face == openFace;
    }

}
