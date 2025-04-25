package rbasamoyai.escalated.handrails;

import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import rbasamoyai.escalated.index.EscalatedBlockEntities;
import rbasamoyai.escalated.walkways.WalkwayHelper;
import rbasamoyai.escalated.walkways.WalkwaySlope;

import java.util.Locale;

public abstract class AbstractHandrailBlock extends HorizontalDirectionalBlock implements IBE<HandrailBlockEntity>, ProperWaterloggedBlock {

    public static final EnumProperty<WalkwayHandrailBlock.Side> SIDE = EnumProperty.create("side", WalkwayHandrailBlock.Side.class);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    protected AbstractHandrailBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.getStateDefinition().any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(FACING, SIDE, WATERLOGGED);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        int width = 1;
        boolean propagateBreak = true;
        if (level.getBlockEntity(pos) instanceof HandrailBlockEntity handrailBE) {
            width = handrailBE.width;
            propagateBreak = handrailBE.propagateBreak;
        }

        super.onRemove(state, level, pos, newState, isMoving);

        if (level.isClientSide || !propagateBreak || state.getBlock() == newState.getBlock() || isMoving)
            return;

        // Forwards and backwards
        for (boolean forward : Iterate.trueAndFalse) {
            BlockPos otherPos = nextSegmentPosition(state, pos, forward);
            if (otherPos == null)
                continue;
            BlockState otherState = level.getBlockState(otherPos);
            if (!(otherState.getBlock() instanceof AbstractHandrailBlock))
                continue;
            if (level.getBlockEntity(otherPos) instanceof HandrailBlockEntity handrailBE)
                handrailBE.width = width; // Propagate this width for blocked breaking
            level.setBlock(otherPos, ProperWaterloggedBlock.withWater(level, Blocks.AIR.defaultBlockState(), otherPos), 3);
            level.levelEvent(2001, otherPos, Block.getId(otherState));
        }

        // Destroy other side if present
        Side side = state.getValue(SIDE);
        if (side != Side.BOTH && width > 1) { // width check used for handrail relocation or mirror deletion
            Direction dir = state.getValue(FACING);
            Direction checkDir = side == Side.LEFT ? dir.getClockWise() : dir.getCounterClockWise();
            BlockPos otherPos = pos.relative(checkDir, width - 1);
            if (level.getBlockEntity(otherPos) instanceof HandrailBlockEntity handrailBE)
                handrailBE.width = 0; // Make sure this code doesn't check on the other side
            level.setBlock(otherPos, ProperWaterloggedBlock.withWater(level, Blocks.AIR.defaultBlockState(), otherPos), 3);
            level.levelEvent(2001, otherPos, Block.getId(level.getBlockState(otherPos)));
        }
    }

    public abstract BlockState getStateForSlope(Level level, BlockPos basePos, BlockState base, BlockPos placePos,
                                                Direction facing, WalkwaySlope slope, Side side);

    @Override public Class<HandrailBlockEntity> getBlockEntityClass() { return HandrailBlockEntity.class; }
    @Override public BlockEntityType<? extends HandrailBlockEntity> getBlockEntityType() { return EscalatedBlockEntities.HANDRAIL.get(); }

    public abstract WalkwaySlope getHandrailSlope(BlockState state);
    public abstract boolean isEndHandrail(BlockState state);

    @Override public FluidState getFluidState(BlockState blockState) { return this.fluidState(blockState); }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !player.mayBuild())
            return InteractionResult.PASS;
        ItemStack heldItem = player.getItemInHand(hand);

        boolean isDye = WalkwayHelper.isDye(heldItem);
        boolean hasWater = WalkwayHelper.hasWater(level, heldItem);

        if (isDye || hasWater)
            return onBlockEntityUse(level, pos, be -> be.setHandrailColor(WalkwayHelper.getDyeColorFromItem(heldItem))
                    ? InteractionResult.SUCCESS : InteractionResult.PASS);

        boolean isGlass = heldItem.is(Blocks.GLASS.asItem());
        if (isGlass && this.canConvertToGlassHandrail()) {
            this.convertToGlass(state, level, pos);
            return InteractionResult.SUCCESS;
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    protected void convertToGlass(BlockState state, Level level, BlockPos pos) {
        int MAX_ITER = 1100;

        Direction facing = state.getValue(AbstractHandrailBlock.FACING);
        Direction left = facing.getCounterClockWise();
        Side side = state.getValue(AbstractHandrailBlock.SIDE);

        int width = 1;
        if (level.getBlockEntity(pos) instanceof HandrailBlockEntity handrailBE) {
            width = handrailBE.width;
            handrailBE.propagateBreak = false; // Prepare for replacement
        }
        level.setBlock(pos, this.getGlassHandrail(state), 3);
        if (level.getBlockEntity(pos) instanceof HandrailBlockEntity handrailBE)
            handrailBE.width = width;

        BlockPos offset = BlockPos.ZERO;
        if (width > 1) {
            Direction offsetDir = side == AbstractHandrailBlock.Side.LEFT ? left.getOpposite() : left;
            offset = BlockPos.ZERO.relative(offsetDir, width - 1);
            BlockPos offsetPos = pos.offset(offset);
            if (level.getBlockEntity(offsetPos) instanceof HandrailBlockEntity handrailBE) {
                handrailBE.propagateBreak = false;
                level.setBlock(offsetPos, this.getGlassHandrail(level.getBlockState(offsetPos)), 3);
                if (level.getBlockEntity(offsetPos) instanceof HandrailBlockEntity handrailBE1)
                    handrailBE1.width = width;
            }
        }

        for (boolean forward : Iterate.trueAndFalse) {
            BlockPos iterPos = pos;
            BlockState iterState = state;

            for (int i = 0; i < MAX_ITER; ++i) {
                iterPos = AbstractHandrailBlock.nextSegmentPosition(iterState, iterPos, forward);
                if (iterPos == null)
                    break;
                iterState = level.getBlockState(iterPos);
                if (!(iterState.getBlock() instanceof AbstractHandrailBlock))
                    break;
                if (level.getBlockEntity(iterPos) instanceof HandrailBlockEntity other) {
                    other.propagateBreak = false;
                    level.setBlock(iterPos, this.getGlassHandrail(iterState), 3);
                    if (level.getBlockEntity(iterPos) instanceof HandrailBlockEntity other1)
                        other1.width = width;
                }
                BlockPos offsetPos = iterPos.offset(offset);
                if (width > 1 && level.getBlockEntity(offsetPos) instanceof HandrailBlockEntity other) {
                    other.propagateBreak = false;
                    level.setBlock(offsetPos, this.getGlassHandrail(level.getBlockState(offsetPos)), 3);
                    if (level.getBlockEntity(offsetPos) instanceof HandrailBlockEntity other1)
                        other1.width = width;
                }
            }
        }
    }

    protected abstract boolean canConvertToGlassHandrail();
    protected abstract BlockState getGlassHandrail(BlockState state);

    public enum Side implements StringRepresentable {
        LEFT,
        RIGHT,
        BOTH;

        private final String id = this.name().toLowerCase(Locale.ROOT);

        @Override public String getSerializedName() { return this.id; }
    }

    public static BlockPos nextSegmentPosition(BlockState state, BlockPos pos, boolean forward) {
        AbstractHandrailBlock handrail = (AbstractHandrailBlock) state.getBlock();
        Direction direction = state.getValue(FACING);
        WalkwaySlope slope = handrail.getHandrailSlope(state);
        boolean isEnd = handrail.isEndHandrail(state);

        int offset = forward ? 1 : -1;

        if (slope == WalkwaySlope.TERMINAL && isEnd == forward)
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
