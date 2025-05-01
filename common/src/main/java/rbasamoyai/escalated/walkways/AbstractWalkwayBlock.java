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
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import rbasamoyai.escalated.handrails.AbstractHandrailBlock;
import rbasamoyai.escalated.handrails.HandrailBlockEntity;
import rbasamoyai.escalated.index.EscalatedBlockEntities;
import rbasamoyai.escalated.index.EscalatedTriggers;

import java.util.ArrayList;
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
        Direction facing = this.getFacing(state);
        Direction left = facing.getCounterClockWise();
        boolean connectedLeft = this.connectedToWalkwayOnSide(level, state, pos, left);
        boolean connectedRight = this.connectedToWalkwayOnSide(level, state, pos, left.getOpposite());

        super.onRemove(state, level, pos, newState, isMoving);

        if (level.isClientSide || state.getBlock() == newState.getBlock() || this.getWalkwaySet().blockInSet(newState) || isMoving)
            return;

        WalkwayBlock walkwayBlock = this;
        BlockState currentState = state;

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

        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);
        if (aboveState.getBlock() instanceof AbstractHandrailBlock) {
            AbstractHandrailBlock.Side side = aboveState.getValue(AbstractHandrailBlock.SIDE);
            if (side == AbstractHandrailBlock.Side.BOTH) {
                // Destroy walkway above if narrow
                level.destroyBlock(abovePos, true);
            } else {
                // Move walkway above one block in

                // Get handrail positions
                List<BlockPos> positions = new ArrayList<>();
                positions.add(abovePos);
                BlockPos iterPos = abovePos;
                BlockState iterState = aboveState;
                int MAX_ITER = 1100;
                for (boolean forward : Iterate.trueAndFalse) {
                    iterPos = abovePos;
                    iterState = aboveState;
                    for (int i = 0; i < MAX_ITER; ++i) {
                        iterPos = AbstractHandrailBlock.nextSegmentPosition(iterState, iterPos, forward);
                        if (iterPos == null)
                            break;
                        iterState = level.getBlockState(iterPos);
                        if (!(iterState.getBlock() instanceof AbstractHandrailBlock))
                            break;
                        positions.add(iterPos);
                    }
                }

                if (!(level.getBlockEntity(abovePos) instanceof HandrailBlockEntity handrailBE))
                    return;
                int handrailWidth = handrailBE.width;
                Direction handrailLeft = aboveState.getValue(AbstractHandrailBlock.FACING).getCounterClockWise();
                Direction offsetDir = side == AbstractHandrailBlock.Side.LEFT ? handrailLeft.getOpposite() : handrailLeft;
                if (handrailWidth == 2) { // Narrow handrail placement
                    for (BlockPos handrailPos : positions) {
                        BlockPos newHandrailPos = handrailPos.relative(offsetDir);
                        BlockState oldHandrailState = level.getBlockState(newHandrailPos);
                        if (!(oldHandrailState.getBlock() instanceof AbstractHandrailBlock))
                            continue;
                        level.setBlock(newHandrailPos, ProperWaterloggedBlock.withWater(level,
                                oldHandrailState.setValue(AbstractHandrailBlock.SIDE, AbstractHandrailBlock.Side.BOTH), newHandrailPos), 3);
                        if (level.getBlockEntity(handrailPos) instanceof HandrailBlockEntity handrailBE1)
                            handrailBE1.propagateBreak = false;
                        level.setBlock(handrailPos, ProperWaterloggedBlock.withWater(level, Blocks.AIR.defaultBlockState(), handrailPos), 3);
                        level.levelEvent(2001, handrailPos, Block.getId(oldHandrailState));
                    }
                } else { // Wide placement
                    // Check positions if wide
                    for (BlockPos handrailPos : positions) {
                        BlockPos newHandrailPos = handrailPos.relative(offsetDir);
                        if (!level.getBlockState(newHandrailPos).canBeReplaced()) {
                            // Destroy handrail if blocked
                            level.destroyBlock(abovePos, true);
                            return;
                        }
                    }
                    // Place handrail if successful
                    for (BlockPos handrailPos : positions) {
                        BlockPos newHandrailPos = handrailPos.relative(offsetDir);
                        BlockState placeState = level.getBlockState(handrailPos);
                        level.destroyBlock(newHandrailPos, true);
                        level.setBlock(newHandrailPos, ProperWaterloggedBlock.withWater(level, placeState, newHandrailPos), 3);
                        if (level.getBlockEntity(newHandrailPos) instanceof HandrailBlockEntity newHandrailBE
                            && level.getBlockEntity(handrailPos) instanceof HandrailBlockEntity oldHandrailBE) {
                            newHandrailBE.width = handrailWidth - 1;
                            oldHandrailBE.width = 0; // Use to prevent deletion of other stuff (see AbstractHandrailBlock#onRemove)
                            oldHandrailBE.propagateBreak = false;
                            if (level.getBlockEntity(newHandrailPos.relative(offsetDir, handrailWidth - 1)) instanceof HandrailBlockEntity oppositeHandrailBE) {
                                // Set opposite handrail width
                                // If width == 1 this just sets the same BE, that's OK
                                oppositeHandrailBE.width = newHandrailBE.width;
                            }
                        }
                        level.setBlock(handrailPos, ProperWaterloggedBlock.withWater(level, Blocks.AIR.defaultBlockState(), handrailPos), 3);
                        level.levelEvent(2001, abovePos, Block.getId(placeState));
                    }
                }
            }
        } else if (connectedLeft && connectedRight) {
            // Change walkway placement from splitting
            for (Direction dir : Iterate.directionsInAxis(left.getAxis())) {
                // Find walkway ends
                int MAX_ITER = 256;
                int offset = 1;
                BlockPos offsetPos = pos.relative(dir, offset);
                BlockState offsetState = level.getBlockState(offsetPos);

                // Get end and width (offset)
                for ( ; offset < MAX_ITER; ++offset) {
                    offsetPos = pos.relative(dir, offset);
                    offsetState = level.getBlockState(offsetPos);
                    if (!(offsetState.getBlock() instanceof WalkwayBlock offsetWalkway))
                        return;
                    if (!offsetWalkway.connectedToWalkwayOnSide(level, offsetState, offsetPos, dir))
                        break;
                }
                BlockPos abovePos1 = offsetPos.above();
                BlockState aboveState1 = level.getBlockState(abovePos1);
                if (!(aboveState1.getBlock() instanceof AbstractHandrailBlock)
                    || !(level.getBlockEntity(abovePos1) instanceof HandrailBlockEntity handrailBE)
                    || handrailBE.width == offset)
                    continue;

                // Get all positions
                List<BlockPos> positions = new ArrayList<>();
                BlockPos iterPos;
                BlockState iterState;
                positions.add(abovePos1);
                for (boolean forward : Iterate.trueAndFalse) {
                    iterPos = abovePos1;
                    iterState = aboveState1;
                    for (int i = 0; i < MAX_ITER; ++i) {
                        iterPos = AbstractHandrailBlock.nextSegmentPosition(iterState, iterPos, forward);
                        if (iterPos == null)
                            break;
                        iterState = level.getBlockState(iterPos);
                        if (!(iterState.getBlock() instanceof AbstractHandrailBlock))
                            break;
                        positions.add(iterPos);
                    }
                }

                if (offset > 1) { // Wide handrails
                    // Check other side for blocking things
                    boolean fail = false;
                    for (BlockPos handrailPos : positions) {
                        BlockPos otherHandrailPos = handrailPos.relative(dir, -offset + 1);
                        if (!level.getBlockState(otherHandrailPos).canBeReplaced()) {
                            fail = true;
                            break;
                        }
                    }
                    if (fail) { // Break blocks if cannot place
                        for (BlockPos handrailPos : positions) {
                            if (level.getBlockEntity(handrailPos) instanceof HandrailBlockEntity handrailBE1)
                                handrailBE1.propagateBreak = false;
                            level.destroyBlock(handrailPos, true);
                        }
                        continue; // Check other side
                    } else { // Successful, place other side and set widths
                        for (BlockPos handrailPos : positions) {
                            if (level.getBlockEntity(handrailPos) instanceof HandrailBlockEntity handrailBE1)
                                handrailBE1.width = offset; // Set width on source handrail
                            // Place other side
                            BlockState srcHandrailState = level.getBlockState(handrailPos);
                            BlockPos otherHandrailPos = handrailPos.relative(dir, -offset + 1);
                            level.destroyBlock(otherHandrailPos, true);
                            AbstractHandrailBlock.Side side = srcHandrailState.getValue(AbstractHandrailBlock.SIDE);
                            AbstractHandrailBlock.Side placeSide = side == AbstractHandrailBlock.Side.LEFT ?
                                    AbstractHandrailBlock.Side.RIGHT : AbstractHandrailBlock.Side.LEFT;
                            level.setBlock(otherHandrailPos, ProperWaterloggedBlock.withWater(level,
                                    srcHandrailState.setValue(AbstractHandrailBlock.SIDE, placeSide), otherHandrailPos), 3);
                            if (level.getBlockEntity(otherHandrailPos) instanceof HandrailBlockEntity handrailBE1)
                                handrailBE1.width = offset; // Set width on placed handrail
                        }
                    }
                } else { // Place narrow handrails; no checks needed
                    for (BlockPos handrailPos : positions) {
                        BlockState handrailState = level.getBlockState(handrailPos);
                        level.setBlock(handrailPos, ProperWaterloggedBlock.withWater(level, handrailState.setValue(AbstractHandrailBlock.SIDE,
                                AbstractHandrailBlock.Side.BOTH), handrailPos), 3);
                        if (level.getBlockEntity(handrailPos) instanceof HandrailBlockEntity handrailBE1)
                            handrailBE1.width = 1; // Change width
                    }
                }
            }
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

        boolean isBelt = WalkwayHelper.isHandrail(heldItem);
        if (isBelt)
            return placeHandrail(level, state, pos, player, heldItem) ? InteractionResult.SUCCESS : InteractionResult.PASS;

        return super.use(state, level, pos, player, hand, hitResult);
    }

    protected boolean placeHandrail(Level level, BlockState state, BlockPos pos, Player player, ItemStack itemStack) {
        BlockPos centralPos = pos;
        BlockState referenceState = state;
        if (this.getWalkwaySlope(referenceState) == WalkwaySlope.TERMINAL) {
            centralPos = pos.relative(this.getFacing(referenceState));
            referenceState = level.getBlockState(centralPos);
            if (!(referenceState.getBlock() instanceof WalkwayBlock))
                return false;
        }
        WalkwayBlock walkway = (WalkwayBlock) referenceState.getBlock();
        Direction dir = walkway.getFacing(referenceState);
        AbstractHandrailBlock handrail = (AbstractHandrailBlock) walkway.getWalkwaySet().getHandrailBlock(level, referenceState, centralPos).getBlock();
        int MAX_ITER = 1100;

        if (!walkway.connectedToWalkwayOnSide(level, referenceState, pos, dir.getClockWise())
            && !walkway.connectedToWalkwayOnSide(level, referenceState, pos, dir.getCounterClockWise())) {
            // Place narrow
            if (level.getBlockState(centralPos.above()).getBlock() instanceof AbstractHandrailBlock)
                return false;
            List<BlockPos> positions = new ArrayList<>();
            BlockState iterState = referenceState;
            BlockPos iterPos = centralPos;
            positions.add(iterPos);
            for (boolean forward : Iterate.trueAndFalse) {
                iterState = referenceState;
                iterPos = centralPos;
                for (int i = 0; i < MAX_ITER; ++i) {
                    iterPos = WalkwayBlock.nextSegmentPosition(iterState, iterPos, forward, true);
                    if (iterPos == null)
                        break;
                    iterState = level.getBlockState(iterPos);
                    if (!(iterState.getBlock() instanceof WalkwayBlock) || !level.getBlockState(iterPos.above()).canBeReplaced())
                        return false;
                    positions.add(iterPos);
                }
            }
            for (BlockPos basePos : positions) {
                BlockState baseState = level.getBlockState(basePos);
                WalkwayBlock otherWalkway = (WalkwayBlock) baseState.getBlock();
                BlockPos abovePos = basePos.above();
                BlockState placeState = handrail.getStateForSlope(level, basePos, baseState, abovePos, dir,
                        otherWalkway.getWalkwaySlope(baseState), AbstractHandrailBlock.Side.BOTH);
                level.destroyBlock(abovePos, true);
                level.setBlock(abovePos, placeState, 3);
            }
            level.playSound(null, pos.above(), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 1F);
            EscalatedTriggers.HANDRAIL.tryAwardingTo(player);
            return true;
        } else {
            // Place wide
            Direction left = dir.getCounterClockWise();
            for (int i = 0; i < MAX_ITER; ++i) { // Search for left end
                BlockPos leftPos = centralPos.relative(left, i);
                BlockState leftState = level.getBlockState(leftPos);
                if (!(leftState.getBlock() instanceof WalkwayBlock leftWalkway))
                    return false;
                if (leftWalkway.connectedToWalkwayOnSide(level, leftState, leftPos, left))
                    continue;
                referenceState = leftState;
                centralPos = leftPos;
                break;
            }
            if (!(level.getBlockEntity(centralPos) instanceof WalkwayBlockEntity centralBE))
                return false;
            if (level.getBlockState(centralPos.above()).getBlock() instanceof AbstractHandrailBlock)
                return false;

            // Place left side
            List<BlockPos> leftPositions = new ArrayList<>();
            BlockState iterState = referenceState;
            BlockPos iterPos = centralPos;
            leftPositions.add(iterPos);
            for (boolean forward : Iterate.trueAndFalse) {
                iterState = referenceState;
                iterPos = centralPos;
                for (int i = 0; i < MAX_ITER; ++i) { // Forwards
                    iterPos = WalkwayBlock.nextSegmentPosition(iterState, iterPos, forward, true);
                    if (iterPos == null)
                        break;
                    iterState = level.getBlockState(iterPos);
                    if (!(iterState.getBlock() instanceof WalkwayBlock) || !level.getBlockState(iterPos.above()).canBeReplaced())
                        return false;
                    leftPositions.add(iterPos);
                }
            }

            // Check right side
            WalkwayBlockEntity controllerBE = centralBE.getControllerBE();
            if (controllerBE == null)
                return false;
            int width = controllerBE.walkwayWidth;
            int offset = width - 1;
            centralPos = centralPos.relative(left, -offset);
            referenceState = level.getBlockState(centralPos);
            if (!(referenceState.getBlock() instanceof WalkwayBlock))
                return false; // Should never happen if walkway is properly formatted
            for (BlockPos leftBasePos : leftPositions) { // Check if handrails can be placed
                BlockPos rightAbovePos = leftBasePos.relative(left, -offset).above();
                if (!level.getBlockState(leftBasePos.above()).canBeReplaced() || !level.getBlockState(rightAbovePos).canBeReplaced())
                    return false;
            }

            // Place if all positions are valid
            for (BlockPos leftBasePos : leftPositions) {
                BlockState leftBaseState = level.getBlockState(leftBasePos);
                WalkwayBlock otherWalkway = (WalkwayBlock) leftBaseState.getBlock();
                BlockPos leftAbovePos = leftBasePos.above();

                BlockState leftPlaceState = handrail.getStateForSlope(level, leftBasePos, leftBaseState, leftAbovePos,
                        dir, otherWalkway.getWalkwaySlope(leftBaseState), AbstractHandrailBlock.Side.LEFT);
                level.destroyBlock(leftAbovePos, true);
                level.setBlock(leftAbovePos, ProperWaterloggedBlock.withWater(level, leftPlaceState, leftAbovePos), 3);
                if (level.getBlockEntity(leftAbovePos) instanceof HandrailBlockEntity handrailBE)
                    handrailBE.width = width;

                BlockPos rightBasePos = leftBasePos.relative(left, -offset);
                BlockState rightBaseState = level.getBlockState(rightBasePos);
                BlockPos rightAbovePos = rightBasePos.above();
                BlockState rightPlaceState = handrail.getStateForSlope(level, rightBasePos, rightBaseState, rightAbovePos,
                        dir, otherWalkway.getWalkwaySlope(rightBaseState), AbstractHandrailBlock.Side.RIGHT);
                level.destroyBlock(rightAbovePos, true);
                level.setBlock(rightAbovePos, ProperWaterloggedBlock.withWater(level, rightPlaceState, rightAbovePos), 3);
                if (level.getBlockEntity(rightAbovePos) instanceof HandrailBlockEntity handrailBE)
                    handrailBE.width = width;
            }
            level.playSound(null, pos.above(), SoundEvents.WOOL_PLACE, SoundSource.BLOCKS, 0.5F, 1F);
            EscalatedTriggers.HANDRAIL.tryAwardingTo(player);
            return true;
        }
    }

    @Override
    public WalkwaySet getWalkwaySet() {
        if (this.walkwaySet == null)
            this.walkwaySet = this.walkwaySetSupplier.get();
        return this.walkwaySet;
    }

}
