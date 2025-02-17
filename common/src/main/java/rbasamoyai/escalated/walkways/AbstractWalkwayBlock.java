package rbasamoyai.escalated.walkways;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.equipment.armor.DivingBootsItem;
import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.utility.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import rbasamoyai.escalated.index.EscalatedBlockEntities;

import java.util.List;

public abstract class AbstractWalkwayBlock extends HorizontalKineticBlock implements IBE<WalkwayBlockEntity>, WalkwayBlock {

    protected AbstractWalkwayBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
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
        this.transportEntity(level, pos, state, entity);
    }

    protected void transportEntity(Level level, BlockPos pos, BlockState state, Entity entity) {
//        if (!canTransportObjects(state)) TODO item transport?
//            return;
        if (entity instanceof Player player && (player.isShiftKeyDown() || player.getAbilities().flying))
            return;
        if (DivingBootsItem.isWornBy(entity))
            return;
        WalkwayBlockEntity walkway = WalkwayHelper.getSegmentBE(level, pos);
        if (walkway == null)
            return;

        // TODO item transport?
//        if (entityIn instanceof ItemEntity && entityIn.isAlive()) {
//            if (worldIn.isClientSide)
//                return;
//            if (entityIn.getDeltaMovement().y > 0)
//                return;
//            if (!entityIn.isAlive())
//                return;
//            if (BeltTunnelInteractionHandler.getTunnelOnPosition(worldIn, pos) != null)
//                return;
//            withBlockEntityDo(worldIn, pos, be -> {
//                ItemEntity itemEntity = (ItemEntity) entityIn;
//                IItemHandler handler = be.getCapability(ForgeCapabilities.ITEM_HANDLER)
//                        .orElse(null);
//                if (handler == null)
//                    return;
//                ItemStack remainder = handler.insertItem(0, itemEntity.getItem()
//                        .copy(), false);
//                if (remainder.isEmpty())
//                    itemEntity.discard();
//                else if (remainder.getCount() != itemEntity.getItem().getCount())
//                    itemEntity.setItem(remainder);
//            });
//            return;
//        }

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

        if (level.isClientSide || state.getBlock() == newState.getBlock() || isMoving)
            return;

        WalkwayBlock walkwayBlock = this;
        BlockState currentState = state;

        // Destroy chain
        for (boolean forward : Iterate.trueAndFalse) {
            BlockPos currentPos = WalkwayBlock.nextSegmentPosition(state, pos, forward, walkwayBlock.isTerminal(currentState));
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

}
