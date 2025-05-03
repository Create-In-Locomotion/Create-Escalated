package rbasamoyai.escalated.walkways;

import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;

public class WalkwayTerminalBlock extends AbstractWalkwayBlock {

    public WalkwayTerminalBlock(Properties properties, NonNullSupplier<WalkwaySet> walkwaySetSupplier) {
        super(properties, walkwaySetSupplier);
        this.registerDefaultState(this.getStateDefinition().any().setValue(CAPS_SHAFT, WalkwayCaps.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CAPS_SHAFT);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        Direction leftFace = state.getValue(HORIZONTAL_FACING).getCounterClockWise();
        WalkwayCaps caps = state.getValue(CAPS_SHAFT);
        return face == leftFace && !caps.hasLeftCap() || face == leftFace.getOpposite() && !caps.hasRightCap() || face == Direction.DOWN;
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        return super.areStatesKineticallyEquivalent(oldState, newState) && oldState.getValue(CAPS_SHAFT) == newState.getValue(CAPS_SHAFT);
    }

    @Override public WalkwaySlope getWalkwaySlope(BlockState state) { return WalkwaySlope.TERMINAL; }

    @Override public boolean hasWalkwayShaft(BlockState state) { return true; }

    @Override public boolean movesEntities(BlockState state) { return false; }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockState newState = state;

        Direction facing = state.getValue(HORIZONTAL_FACING);
        Direction left = facing.getCounterClockWise();
        Direction right = left.getOpposite();
        Direction clicked = context.getClickedFace();
        WalkwayCaps caps = state.getValue(CAPS_SHAFT);

        if (clicked == left)
            caps = caps.toggleLeft();
        if (clicked == right)
            caps = caps.toggleRight();

        newState = newState.setValue(CAPS_SHAFT, caps);
        KineticBlockEntity.switchToBlockState(level, context.getClickedPos(), this.updateAfterWrenched(newState, context));

        BlockState setState = level.getBlockState(context.getClickedPos());
        if (setState != state) {
            this.playRotateSound(level, context.getClickedPos());
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    @Override
    public BlockState transformFromMerge(Level level, BlockState state, BlockPos pos, boolean left, boolean shaft, boolean remove, boolean replace) {
        if (remove)
            return state;
        WalkwayCaps caps = state.getValue(CAPS_SHAFT);
        if (replace) {
            if (left && caps.hasRightCap()) {
                caps = caps.toggleRight();
            } else if (!left && caps.hasLeftCap()) {
                caps = caps.toggleLeft();
            }
        } else {
            if (left && caps.hasRightCap()) {
                caps = caps.toggleRight();
            } else if (!left && caps.hasLeftCap()) {
                caps = caps.toggleLeft();
            }
        }
        return state.setValue(CAPS_SHAFT, caps);
    }

    @Override
    public boolean connectedToWalkwayOnSide(Level level, BlockState state, BlockPos pos, Direction face) {
        Direction facing = state.getValue(HORIZONTAL_FACING);
        if (face == facing)
            return true;
        if (face == facing.getOpposite())
            return false;
        BlockPos otherPos = pos.relative(facing);
        BlockState otherState = level.getBlockState(otherPos);
        if (otherState.getBlock() instanceof WalkwayBlock walkway && !(otherState.getBlock() instanceof WalkwayTerminalBlock)
            && walkway.connectedToWalkwayOnSide(level, otherState, otherPos, face))
            return true;
        return false;
    }

    @Override
    public boolean isEscalator(Level level, BlockState state, BlockPos pos) {
        Direction facing = state.getValue(HORIZONTAL_FACING);
        BlockPos otherPos = pos.relative(facing);
        BlockState otherState = level.getBlockState(otherPos);
        if (!(otherState.getBlock() instanceof WalkwayBlock walkway))
            return false;
        if (walkway.getWalkwaySlope(otherState) == WalkwaySlope.TERMINAL)
            return false;
        return walkway.isEscalator(level, state, pos);
    }

}
