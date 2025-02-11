package rbasamoyai.escalated.walkways;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import rbasamoyai.escalated.index.EscalatedBlockEntities;

import java.util.Locale;

public class WalkwayTerminalBlock extends HorizontalKineticBlock implements IBE<WalkwayTerminalBlockEntity> {

    public static final EnumProperty<Caps> CAPS = EnumProperty.create("caps", Caps.class);

    public WalkwayTerminalBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(CAPS, Caps.NONE));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CAPS);
    }

    @Override public Direction.Axis getRotationAxis(BlockState state) { return state.getValue(HORIZONTAL_FACING).getClockWise().getAxis(); }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        Direction leftFace = state.getValue(HORIZONTAL_FACING).getCounterClockWise();
        Caps caps = state.getValue(CAPS);
        return face == leftFace && !caps.hasLeftCap() || face == leftFace.getOpposite() && !caps.hasRightCap() || face == Direction.DOWN;
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        return super.areStatesKineticallyEquivalent(oldState, newState) && oldState.getValue(CAPS) == newState.getValue(CAPS);
    }

    @Override public Class<WalkwayTerminalBlockEntity> getBlockEntityClass() { return WalkwayTerminalBlockEntity.class; }
    @Override public BlockEntityType<? extends WalkwayTerminalBlockEntity> getBlockEntityType() { return EscalatedBlockEntities.WALKWAY_TERMINAL.get(); }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockState newState = state;

        Direction facing = state.getValue(HORIZONTAL_FACING);
        Direction left = facing.getCounterClockWise();
        Direction right = left.getOpposite();
        Direction clicked = context.getClickedFace();
        Caps caps = state.getValue(CAPS);

        if (clicked == left)
            caps = caps.toggleLeft();
        if (clicked == right)
            caps = caps.toggleRight();

        newState = newState.setValue(CAPS, caps);
        KineticBlockEntity.switchToBlockState(level, context.getClickedPos(), this.updateAfterWrenched(newState, context));

        BlockState setState = level.getBlockState(context.getClickedPos());
        if (setState != state) {
            this.playRotateSound(level, context.getClickedPos());
            return InteractionResult.SUCCESS;
        } else {
            return InteractionResult.PASS;
        }
    }

    public enum Caps implements StringRepresentable {
        NONE,
        LEFT,
        RIGHT,
        BOTH;

        private final String name = this.name().toLowerCase(Locale.ROOT);

        public boolean hasLeftCap() { return this == LEFT || this == BOTH; }
        public boolean hasRightCap() { return this == RIGHT || this == BOTH; }

        public Caps toggleLeft() {
            return switch (this) {
                case NONE -> LEFT;
                case LEFT -> NONE;
                case RIGHT -> BOTH;
                case BOTH -> RIGHT;
            };
        }

        public Caps toggleRight() {
            return switch (this) {
                case NONE -> RIGHT;
                case LEFT -> BOTH;
                case RIGHT -> NONE;
                case BOTH -> LEFT;
            };
        }

        @Override public String getSerializedName() { return this.name; }
    }

}
