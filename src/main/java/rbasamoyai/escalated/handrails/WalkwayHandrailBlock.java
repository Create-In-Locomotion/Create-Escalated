package rbasamoyai.escalated.handrails;

import com.simibubi.create.foundation.utility.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.escalated.index.EscalatedBlocks;
import rbasamoyai.escalated.index.EscalatedShapes;
import rbasamoyai.escalated.walkways.WalkwayBlock;
import rbasamoyai.escalated.walkways.WalkwaySlope;

import java.util.Locale;

public class WalkwayHandrailBlock extends AbstractHandrailBlock {

    public static final EnumProperty<Part> PART = EnumProperty.create("part", Part.class);

    public WalkwayHandrailBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(PART);
    }

    @Override
    public BlockState getStateForSlope(Level level, BlockPos basePos, BlockState base, BlockPos placePos,
                                       Direction facing, WalkwaySlope slope, Side side) {
        BlockState returnState = this.defaultBlockState().setValue(FACING, facing).setValue(SIDE, side);
        if (slope != WalkwaySlope.TERMINAL)
            return returnState.setValue(PART, Part.MIDDLE);
        boolean isStart = ((WalkwayBlock) base.getBlock()).getFacing(base) == facing;
        return returnState.setValue(PART, isStart ? Part.START : Part.END);
    }

    @Override
    public WalkwaySlope getHandrailSlope(BlockState state) {
        return state.getValue(PART) == Part.MIDDLE ? WalkwaySlope.HORIZONTAL : WalkwaySlope.TERMINAL;
    }

    @Override public boolean isEndHandrail(BlockState state) { return state.getValue(PART) == Part.END; }

    @Override protected boolean canConvertToGlassHandrail() { return this != EscalatedBlocks.GLASS_WALKWAY_HANDRAIL.get(); }

    @Override
    protected BlockState getGlassHandrail(BlockState state) {
        return BlockHelper.copyProperties(state, EscalatedBlocks.GLASS_WALKWAY_HANDRAIL.getDefaultState());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Side side = state.getValue(SIDE);
        Part part = state.getValue(PART);
        Direction facing = state.getValue(FACING);
        return switch (side) {
            case LEFT -> switch (part) {
                case START -> EscalatedShapes.WALKWAY_HANDRAIL_LEFT_START.get(facing);
                case MIDDLE -> EscalatedShapes.WALKWAY_HANDRAIL_LEFT_HORIZONTAL.get(facing);
                case END -> EscalatedShapes.WALKWAY_HANDRAIL_LEFT_END.get(facing);
            };
            case RIGHT -> switch (part) {
                case START -> EscalatedShapes.WALKWAY_HANDRAIL_RIGHT_START.get(facing);
                case MIDDLE -> EscalatedShapes.WALKWAY_HANDRAIL_RIGHT_HORIZONTAL.get(facing);
                case END -> EscalatedShapes.WALKWAY_HANDRAIL_RIGHT_END.get(facing);
            };
            case BOTH -> switch (part) {
                case START -> EscalatedShapes.WALKWAY_HANDRAIL_BOTH_START.get(facing);
                case MIDDLE -> EscalatedShapes.WALKWAY_HANDRAIL_BOTH_HORIZONTAL.get(facing);
                case END -> EscalatedShapes.WALKWAY_HANDRAIL_BOTH_END.get(facing);
            };
        };
    }

    public enum Part implements StringRepresentable {
        START,
        MIDDLE,
        END;

        private final String id = this.name().toLowerCase(Locale.ROOT);

        @Override public String getSerializedName() { return this.id; }
    }

}
