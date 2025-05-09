package rbasamoyai.escalated.walkways;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllSoundEvents;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.escalated.index.EscalatedShapes;

public class WideWalkwaySideBlock extends AbstractWalkwayBlock {

    public static final BooleanProperty LEFT = BooleanProperty.create("left");

    public WideWalkwaySideBlock(Properties properties, NonNullSupplier<WalkwaySet> walkwaySetSupplier) {
        super(properties, walkwaySetSupplier);
        this.registerDefaultState(this.getStateDefinition().any().setValue(CAPS_SIDED, WalkwayCaps.NO_SHAFT));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LEFT, CAPS_SIDED);
    }

    @Override public WalkwaySlope getWalkwaySlope(BlockState state) { return WalkwaySlope.HORIZONTAL; }

    @Override
    public boolean hasWalkwayShaft(BlockState state) {
        return state.getValue(CAPS_SIDED) != WalkwayCaps.NO_SHAFT;
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        Direction facing = state.getValue(HORIZONTAL_FACING);
        facing = state.getValue(LEFT) ? facing.getCounterClockWise() : facing.getClockWise();
        return state.getValue(CAPS_SIDED) == WalkwayCaps.NONE && face == facing;
    }

    @Override
    protected boolean areStatesKineticallyEquivalent(BlockState oldState, BlockState newState) {
        return super.areStatesKineticallyEquivalent(oldState, newState)
                && oldState.getValue(CAPS_SIDED) == newState.getValue(CAPS_SIDED)
                && oldState.getValue(LEFT) == newState.getValue(LEFT);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !player.mayBuild())
            return InteractionResult.PASS;
        ItemStack heldItem = player.getItemInHand(hand);
        if (AllBlocks.SHAFT.isIn(heldItem)) {
            if (state.getValue(CAPS_SIDED) != WalkwayCaps.NO_SHAFT)
                return InteractionResult.PASS;
            if (level.isClientSide)
                return InteractionResult.SUCCESS;
            if (!player.isCreative())
                heldItem.shrink(1);
            KineticBlockEntity.switchToBlockState(level, pos, state.setValue(CAPS_SIDED, WalkwayCaps.NONE));
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
        Direction clicked = context.getClickedFace();
        WalkwayCaps caps = state.getValue(CAPS_SIDED);
        if (caps == WalkwayCaps.NO_SHAFT)
            return InteractionResult.PASS;

        Direction shaftFacing = state.getValue(LEFT) ? facing.getCounterClockWise() : facing.getClockWise();
        if (clicked == shaftFacing)
            caps = caps == WalkwayCaps.NONE ? WalkwayCaps.BOTH : WalkwayCaps.NONE;

        newState = newState.setValue(CAPS_SIDED, caps);
        KineticBlockEntity.switchToBlockState(level, context.getClickedPos(), this.updateAfterWrenched(newState, context));

        BlockState setState = level.getBlockState(context.getClickedPos());
        if (setState != state) {
            AllSoundEvents.WRENCH_REMOVE.playOnServer(level, context.getClickedPos(), 1, level.random.nextFloat() * .5f + .5f);
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

        if (state.getValue(CAPS_SIDED) != WalkwayCaps.NO_SHAFT) {
            if (level.isClientSide)
                return InteractionResult.SUCCESS;
            if (player != null && !player.isCreative())
                player.getInventory().placeItemBackInInventory(AllBlocks.SHAFT.asStack());
            KineticBlockEntity.switchToBlockState(level, pos, state.setValue(CAPS_SIDED, WalkwayCaps.NO_SHAFT));
            AllSoundEvents.WRENCH_REMOVE.playOnServer(level, pos, 1, level.random.nextFloat() * .5f + .5f);
            return InteractionResult.SUCCESS;
        }

        return super.onSneakWrenched(state, context);
    }

    @Override
    public BlockState transformFromMerge(Level level, BlockState state, BlockPos pos, boolean left, boolean shaft, boolean remove, boolean replace) {
        WalkwayCaps caps = state.getValue(CAPS_SIDED);
        if (shaft && caps == WalkwayCaps.NO_SHAFT) {
            caps = WalkwayCaps.NONE;
        } else if (!shaft) {
            caps = WalkwayCaps.NO_SHAFT;
        }
        state = state.setValue(CAPS_SIDED, caps);

        boolean srcLeft = state.getValue(LEFT);
        Direction facing = state.getValue(HORIZONTAL_FACING);
        if (remove) {
            if (srcLeft == left)
                return state;
            return this.getWalkwaySet().getNarrowBlock(level, state, pos)
                    .setValue(HORIZONTAL_FACING, facing)
                    .setValue(NarrowWalkwayBlock.CAPS, caps); // CAPS_SIDED is a subset of CAPS
        } else {
            if (srcLeft != left)
                return state;
            return this.getWalkwaySet().getWideCenterBlock(level, state, pos)
                    .setValue(HORIZONTAL_FACING, facing)
                    .setValue(WideWalkwayCenterBlock.SHAFT, shaft);
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

    @Override public boolean isEscalator(Level level, BlockState state, BlockPos pos) { return false; }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(HORIZONTAL_FACING);
        return state.getValue(LEFT) ? EscalatedShapes.WIDE_WALKWAY_SIDE_LEFT.get(facing) : EscalatedShapes.WIDE_WALKWAY_SIDE_RIGHT.get(facing);
    }

}
