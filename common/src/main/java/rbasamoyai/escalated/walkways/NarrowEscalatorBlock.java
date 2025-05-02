package rbasamoyai.escalated.walkways;

import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.escalated.index.EscalatedShapes;

public class NarrowEscalatorBlock extends AbstractWalkwayBlock {

    public NarrowEscalatorBlock(Properties properties, NonNullSupplier<WalkwaySet> walkwaySetSupplier) {
        super(properties, walkwaySetSupplier);
        this.registerDefaultState(this.getStateDefinition().any().setValue(SLOPE, WalkwaySlope.HORIZONTAL));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SLOPE);
    }

    @Override public WalkwaySlope getWalkwaySlope(BlockState state) { return state.getValue(SLOPE); }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        WalkwaySlope slope = state.getValue(SLOPE);
        Direction facing = state.getValue(HORIZONTAL_FACING);
        return switch (slope) {
            case BOTTOM -> EscalatedShapes.NARROW_ESCALATOR_BOTTOM.get(facing);
            case TOP -> EscalatedShapes.NARROW_ESCALATOR_TOP.get(facing);
            case MIDDLE -> EscalatedShapes.NARROW_ESCALATOR_MIDDLE.get(facing);
            default -> EscalatedShapes.NARROW_WALKWAY.get(facing);
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
            case BOTTOM -> EscalatedShapes.NARROW_ESCALATOR_BOTTOM_STEPPED.getShape(visualOffset, facing);
            case MIDDLE -> EscalatedShapes.NARROW_ESCALATOR_MIDDLE_STEPPED.getShape(visualOffset, facing);
            case TOP -> EscalatedShapes.NARROW_ESCALATOR_TOP_STEPPED.getShape(visualOffset, facing);
            default -> super.getCollisionShape(state, blockGetter, pos, context);
        };
    }

    @Override public boolean hasWalkwayShaft(BlockState state) { return false; }

    @Override
    public BlockState transformFromMerge(Level level, BlockState state, BlockPos pos, boolean left, boolean shaft, boolean remove) {
        if (remove)
            return state;
        return this.getWalkwaySet().getWideSideBlock(level, state, pos)
                .setValue(HORIZONTAL_FACING, state.getValue(HORIZONTAL_FACING))
                .setValue(WideEscalatorSideBlock.LEFT, !left)
                .setValue(SLOPE, state.getValue(SLOPE));
    }

    @Override
    public boolean connectedToWalkwayOnSide(Level level, BlockState state, BlockPos pos, Direction face) {
        return face.getAxis() == state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override public boolean isEscalator(Level level, BlockState state, BlockPos pos) { return true; }

}
