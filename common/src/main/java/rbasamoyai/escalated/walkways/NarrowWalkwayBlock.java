package rbasamoyai.escalated.walkways;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.escalated.index.EscalatedShapes;

public class NarrowWalkwayBlock extends AbstractWalkwayBlock  {

    public NarrowWalkwayBlock(Properties properties, NonNullSupplier<WalkwaySet> walkwaySetSupplier) {
        super(properties, walkwaySetSupplier);
        this.registerDefaultState(this.getStateDefinition().any().setValue(CAPS, WalkwayCaps.NO_SHAFT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(CAPS);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        Direction leftFace = state.getValue(HORIZONTAL_FACING).getCounterClockWise();
        WalkwayCaps caps = state.getValue(CAPS);
        return face == leftFace && !caps.hasLeftCap() || face == leftFace.getOpposite() && !caps.hasRightCap() || face == Direction.DOWN;
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        return super.areStatesKineticallyEquivalent(oldState, newState) && oldState.getValue(CAPS) == newState.getValue(CAPS);
    }

    @Override public BeltSlope getWalkwaySlope(BlockState state) { return BeltSlope.HORIZONTAL; }

    @Override public boolean hasWalkwayShaft(BlockState state) { return state.getValue(CAPS) != WalkwayCaps.NO_SHAFT; }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !player.mayBuild())
            return InteractionResult.PASS;
        ItemStack heldItem = player.getItemInHand(hand);
        if (AllBlocks.SHAFT.isIn(heldItem)) {
            if (state.getValue(CAPS) != WalkwayCaps.NO_SHAFT)
                return InteractionResult.PASS;
            if (level.isClientSide)
                return InteractionResult.SUCCESS;
            if (!player.isCreative())
                heldItem.shrink(1);
            KineticBlockEntity.switchToBlockState(level, pos, state.setValue(CAPS, WalkwayCaps.NONE));
            AllBlocks.SHAFT.get().playEncaseSound(level, pos);
            return InteractionResult.SUCCESS;
        }

        return super.use(state, level, pos, player, hand, hitResult);
    }

    @Override
    public InteractionResult onWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockState newState = state;

        Direction facing = state.getValue(HORIZONTAL_FACING);
        Direction left = facing.getCounterClockWise();
        Direction right = left.getOpposite();
        Direction clicked = context.getClickedFace();
        WalkwayCaps caps = state.getValue(CAPS);
        if (caps == WalkwayCaps.NO_SHAFT)
            return InteractionResult.PASS;

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

    @Override
    public InteractionResult onSneakWrenched(BlockState state, UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (state.getValue(CAPS) != WalkwayCaps.NO_SHAFT) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;
            if (player != null && !player.isCreative())
                player.getInventory().placeItemBackInInventory(AllBlocks.SHAFT.asStack());
            KineticBlockEntity.switchToBlockState(level, pos, state.setValue(CAPS, WalkwayCaps.NO_SHAFT));
            this.playRemoveSound(level, pos);
            return InteractionResult.SUCCESS;
        }

        return super.onSneakWrenched(state, context);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return EscalatedShapes.NARROW_WALKWAY.get(state.getValue(HORIZONTAL_FACING));
    }

    @Override
    public BlockState transformFromMerge(Level level, BlockState state, BlockPos pos, boolean left, boolean shaft, boolean remove) {
        if (remove)
            return state;
        WalkwayCaps caps = shaft ? WalkwayCaps.NONE : WalkwayCaps.NO_SHAFT;
        WalkwayCaps srcCaps = state.getValue(CAPS);
        if (shaft && (srcCaps.hasRightCap() && left || srcCaps.hasLeftCap() && !left))
            caps = WalkwayCaps.BOTH;
        return this.getWalkwaySet().getWideSideBlock(level, state, pos)
                .setValue(HORIZONTAL_FACING, state.getValue(HORIZONTAL_FACING))
                .setValue(WideWalkwaySideBlock.CAPS_SIDED, caps)
                .setValue(WideWalkwaySideBlock.LEFT, !left);
    }

    @Override
    public boolean connectedToWalkwayOnSide(Level level, BlockState state, BlockPos pos, Direction face) {
        return face.getAxis() == state.getValue(HORIZONTAL_FACING).getAxis();
    }

}
