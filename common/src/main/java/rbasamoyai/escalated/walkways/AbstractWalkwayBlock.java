package rbasamoyai.escalated.walkways;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.armor.DivingBootsItem;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.utility.Iterate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import rbasamoyai.escalated.index.EscalatedBlockEntities;

import java.util.List;

public abstract class AbstractWalkwayBlock extends HorizontalKineticBlock implements IBE<WalkwayBlockEntity>, WalkwayBlock {

    private final NonNullSupplier<WalkwaySet> walkwaySetSupplier;
    private WalkwaySet walkwaySet = null;

    protected AbstractWalkwayBlock(Properties properties, NonNullSupplier<WalkwaySet> walkwaySetSupplier) {
        super(properties);
        this.walkwaySetSupplier = walkwaySetSupplier;
    }

    @Override public Direction getFacing(BlockState state) { return state.getValue(HORIZONTAL_FACING); }

    @Override public Direction.Axis getRotationAxis(BlockState state) { return state.getValue(HORIZONTAL_FACING).getClockWise().getAxis(); }

    @Override public Class<WalkwayBlockEntity> getBlockEntityClass() { return WalkwayBlockEntity.class; }
    @Override public BlockEntityType<? extends WalkwayBlockEntity> getBlockEntityType() { return EscalatedBlockEntities.WALKWAY.get(); }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        this.transportEntity(level, pos, state, entity);
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        BlockPos actualPos = entity.getOnPos();
        BlockState actualState = level.getBlockState(actualPos);
        this.transportEntity(level, actualPos, actualState, entity);
    }

    protected void transportEntity(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity instanceof Player player && (player.isShiftKeyDown() || player.getAbilities().flying))
            return;
        if (DivingBootsItem.isWornBy(entity))
            return;
        WalkwayBlockEntity walkway = WalkwayHelper.getSegmentBE(level, pos);
        if (walkway == null)
            return;

        WalkwayBlockEntity controller = WalkwayHelper.getControllerBE(level, pos);
        if (controller == null || controller.passengers == null)
            return;
        if (controller.passengers.containsKey(entity)) {
            WalkwayMovementHandler.TransportedEntityInfo info = controller.passengers.get(entity);
            if (info.getTicksSinceLastCollision() != 0 || pos.equals(entity.blockPosition()))
                info.refresh(pos, state);
        } else {
            controller.passengers.put(entity, new WalkwayMovementHandler.TransportedEntityInfo(pos, state));
            entity.setOnGround(true);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        super.onRemove(state, level, pos, newState, isMoving);

        if (level.isClientSide || state.getBlock() == newState.getBlock() || this.getWalkwaySet().blockInSet(newState) || isMoving)
            return;

        WalkwayBlock walkwayBlock = this;
        BlockState currentState = state;

        Direction facing = walkwayBlock.getFacing(currentState);
        Direction left = facing.getCounterClockWise();

        // At this block
        for (Direction dir : Iterate.directionsInAxis(left.getAxis())) {
            BlockPos sidePos = pos.relative(dir);
            BlockState sideState = level.getBlockState(sidePos);
            if (!(sideState.getBlock() instanceof WalkwayBlock sideWalkway)
                    || !sideWalkway.connectedToWalkwayOnSide(level, sideState, sidePos, dir.getOpposite()))
                continue;
            boolean sideShaft = sideWalkway.hasWalkwayShaft(sideState);
            BlockState transformState = sideWalkway.transformFromMerge(level, sideState, sidePos, dir != left, sideShaft, true);

            DyeColor color = null;
            float visualProgress = 0;
            if (level.getBlockEntity(sidePos) instanceof WalkwayBlockEntity sideWalkwayBE) {
                color = sideWalkwayBE.getColor();
                visualProgress = sideWalkwayBE.getVisualProgress();
            }

            KineticBlockEntity.switchToBlockState(level, sidePos, transformState);

            if (level.getBlockEntity(sidePos) instanceof WalkwayBlockEntity sideWalkwayBE) {
                sideWalkwayBE.applyColor(color);
                sideWalkwayBE.setVisualProgress(visualProgress);
                sideWalkwayBE.resetClientRender = true;
                sideWalkwayBE.notifyUpdate();
            }
        }

        // Destroy chain
        for (boolean forward : Iterate.trueAndFalse) {
            BlockPos currentPos = WalkwayBlock.nextSegmentPosition(state, pos, forward, false);
            if (currentPos == null)
                continue;
            currentState = level.getBlockState(currentPos);
            if (!(currentState.getBlock() instanceof WalkwayBlock newWalkway))
                continue;
            walkwayBlock = newWalkway;

            boolean hasPulley = walkwayBlock.hasWalkwayShaft(currentState);
            level.removeBlockEntity(currentPos);
            BlockState shaftState = AllBlocks.SHAFT.getDefaultState().setValue(BlockStateProperties.AXIS, this.getRotationAxis(currentState));
            level.setBlock(currentPos, ProperWaterloggedBlock.withWater(level, hasPulley ? shaftState : Blocks.AIR.defaultBlockState(), currentPos), 3);
            level.levelEvent(2001, currentPos, Block.getId(currentState));
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        List<ItemStack> drops = super.getDrops(state, builder);
        if (this.hasWalkwayShaft(state))
            drops.addAll(AllBlocks.SHAFT.getDefaultState().getDrops(builder));
        return drops;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hitResult) {
        if (player.isShiftKeyDown() || !player.mayBuild())
            return InteractionResult.PASS;
        ItemStack heldItem = player.getItemInHand(hand);

        boolean isDye = WalkwayHelper.isDye(heldItem);
        boolean hasWater = WalkwayHelper.hasWater(level, heldItem);

        if (isDye || hasWater)
            return onBlockEntityUse(level, pos, be -> be.applyColor(WalkwayHelper.getDyeColorFromItem(heldItem))
                    ? InteractionResult.SUCCESS : InteractionResult.PASS);

        return super.use(state, level, pos, player, hand, hitResult);
    }

    protected WalkwaySet getWalkwaySet() {
        if (this.walkwaySet == null)
            this.walkwaySet = this.walkwaySetSupplier.get();
        return this.walkwaySet;
    }

}
