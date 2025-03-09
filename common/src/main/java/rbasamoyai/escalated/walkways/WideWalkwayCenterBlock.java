package rbasamoyai.escalated.walkways;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import rbasamoyai.escalated.index.EscalatedShapes;

public class WideWalkwayCenterBlock extends AbstractWalkwayBlock {

    public static final BooleanProperty SHAFT = BooleanProperty.create("shaft");

    public WideWalkwayCenterBlock(Properties properties, NonNullSupplier<WalkwaySet> walkwaySetSupplier) {
        super(properties, walkwaySetSupplier);
        this.registerDefaultState(this.getStateDefinition().any().setValue(SHAFT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SHAFT);
    }

    @Override public WalkwaySlope getWalkwaySlope(BlockState state) { return WalkwaySlope.HORIZONTAL; }

    @Override public boolean hasWalkwayShaft(BlockState state) { return state.getValue(SHAFT);}

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
    public BlockState transformFromMerge(Level level, BlockState state, BlockPos pos, boolean left, boolean shaft, boolean remove) {
        Direction facing = state.getValue(HORIZONTAL_FACING);
        if (remove) {
            return this.getWalkwaySet().getWideSideBlock(level, state, pos)
                    .setValue(HORIZONTAL_FACING, facing)
                    .setValue(WideWalkwaySideBlock.LEFT, left)
                    .setValue(NarrowWalkwayBlock.CAPS_SIDED, shaft ? WalkwayCaps.NONE : WalkwayCaps.NO_SHAFT);
        } else {
            return state.setValue(SHAFT, shaft);
        }
    }

    @Override
    public boolean connectedToWalkwayOnSide(Level level, BlockState state, BlockPos pos, Direction face) {
        return face.getAxis().isHorizontal();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
        return EscalatedShapes.WIDE_WALKWAY_CENTER;
    }

}
