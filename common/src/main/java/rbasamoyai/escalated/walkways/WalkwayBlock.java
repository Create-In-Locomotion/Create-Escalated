package rbasamoyai.escalated.walkways;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.levelgen.DebugLevelSource;

import java.util.LinkedList;
import java.util.List;

public interface WalkwayBlock {

    // Common properties
    EnumProperty<WalkwayCaps> CAPS = EnumProperty.create("caps", WalkwayCaps.class);
    EnumProperty<WalkwayCaps> CAPS_SHAFT = EnumProperty.create("caps", WalkwayCaps.class, e -> e != WalkwayCaps.NO_SHAFT);
    EnumProperty<WalkwayCaps> CAPS_SIDED = EnumProperty.create("caps", WalkwayCaps.class, WalkwayCaps.NONE, WalkwayCaps.BOTH, WalkwayCaps.NO_SHAFT);
    EnumProperty<WalkwaySlope> SLOPE = EnumProperty.create("slope", WalkwaySlope.class, s -> s != WalkwaySlope.TERMINAL);

    Direction getFacing(BlockState state);
    WalkwaySlope getWalkwaySlope(BlockState state);
    boolean hasWalkwayShaft(BlockState state);
    BlockState transformFromMerge(Level level, BlockState state, BlockPos pos, boolean left, boolean shaft, boolean remove);
    boolean connectedToWalkwayOnSide(Level level, BlockState state, BlockPos pos, Direction face);
    WalkwaySet getWalkwaySet();
    boolean isEscalator(Level level, BlockState state, BlockPos pos);

    default boolean movesEntities(BlockState state) { return true; }

    static void initWalkway(Level level, BlockPos pos) {
        if (level.isClientSide || level instanceof ServerLevel slevel && slevel.getChunkSource().getGenerator() instanceof DebugLevelSource)
            return;
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof WalkwayBlock))
            return;
        // Find controller
        int limit = 1000;
        boolean escalator = false;
        BlockPos currentPos = pos;

        boolean terminal = false;
        while (limit-- > 0) {
            BlockState currentState = level.getBlockState(currentPos);
            if (!(currentState.getBlock() instanceof WalkwayBlock)) {
                level.destroyBlock(pos, true);
                return;
            }
            BlockPos nextSegmentPosition = nextSegmentPosition(currentState, currentPos, true, terminal);
            terminal = true;
            if (nextSegmentPosition == null)
                break;
            if (!level.isLoaded(nextSegmentPosition))
                return;
            currentPos = nextSegmentPosition;
        }

        // Init belts
        List<BlockPos> walkwayChain = getWalkwayChain(level, currentPos);
        int minSize = escalator ? 5 : 3;
        if (walkwayChain.size() < minSize) {
            level.destroyBlock(currentPos, true);
            return;
        }

        for (BlockPos beltPos : walkwayChain) {
            BlockState currentState = level.getBlockState(beltPos);
            if (level.getBlockEntity(beltPos) instanceof WalkwayBlockEntity walkway && currentState.getBlock() instanceof WalkwayBlock) {
                walkway.setController(currentPos);
                walkway.walkwayLength = walkwayChain.size();
                walkway.attachKinetics();
                walkway.notifyUpdate();
            } else {
                level.destroyBlock(currentPos, true);
                return;
            }
        }
    }

    static List<BlockPos> getWalkwayChain(Level level, BlockPos controllerPos) {
        List<BlockPos> positions = new LinkedList<>();

        BlockState blockState = level.getBlockState(controllerPos);
        if (!(blockState.getBlock() instanceof WalkwayBlock))
            return positions;

        int limit = 1000;
        BlockPos current = controllerPos;
        boolean terminal = false;
        while (limit-- > 0 && current != null) {
            BlockState state = level.getBlockState(current);
            if (!(state.getBlock() instanceof WalkwayBlock))
                break;
            positions.add(current);
            current = nextSegmentPosition(state, current, !terminal, terminal);
            terminal = true;
        }

        return positions;
    }

    static BlockPos nextSegmentPosition(BlockState state, BlockPos pos, boolean forward, boolean terminal) {
        WalkwayBlock walkway = (WalkwayBlock) state.getBlock();
        Direction direction = walkway.getFacing(state);
        WalkwaySlope slope = walkway.getWalkwaySlope(state);

        int offset = forward ? 1 : -1;

        if (slope == WalkwaySlope.TERMINAL && terminal)
            return null;
        pos = pos.relative(direction, offset);
        if (slope == WalkwaySlope.MIDDLE)
            return pos.above(offset);
        if (slope == WalkwaySlope.TOP && !forward)
            return pos.below();
        if (slope == WalkwaySlope.BOTTOM && forward)
            return pos.above();
        return pos;
    }
}
