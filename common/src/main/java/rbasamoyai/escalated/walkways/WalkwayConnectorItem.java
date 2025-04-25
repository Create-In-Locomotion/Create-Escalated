package rbasamoyai.escalated.walkways;

import com.simibubi.create.content.kinetics.base.KineticBlock;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.simpleRelays.AbstractSimpleShaftBlock;
import com.simibubi.create.content.kinetics.simpleRelays.ShaftBlock;
import com.simibubi.create.foundation.block.ProperWaterloggedBlock;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import rbasamoyai.escalated.config.EscalatedConfigs;
import rbasamoyai.escalated.handrails.AbstractHandrailBlock;
import rbasamoyai.escalated.handrails.HandrailBlockEntity;
import rbasamoyai.escalated.index.EscalatedBlocks;

import java.util.*;

/**
 * Adpated from {@link com.simibubi.create.content.kinetics.belt.item.BeltConnectorItem}.
 * If you want to make your own walkway type, extend this item.
 */
public class WalkwayConnectorItem extends BlockItem {

    private final Set<Block> otherBlocks = new HashSet<>();

    public WalkwayConnectorItem(Block primaryBlock, Properties properties, Block... otherBlocks) {
        super(primaryBlock, properties);
        this.otherBlocks.addAll(Arrays.asList(otherBlocks));
    }

    @Override public String getDescriptionId() { return this.getOrCreateDescriptionId(); }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // Copied from BeltConnectorItem#useOn --ritchie
        Player playerEntity = context.getPlayer();
        if (playerEntity != null && playerEntity.isShiftKeyDown()) {
            context.getItemInHand().setTag(null);
            return InteractionResult.SUCCESS;
        }

        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean validAxis = validateAxis(level, pos);

        if (level.isClientSide)
            return validAxis ? InteractionResult.SUCCESS : InteractionResult.FAIL;

        CompoundTag tag = context.getItemInHand().getOrCreateTag();
        BlockPos firstTerminal = null;

        // Remove first if no longer existant or valid
        if (tag.contains("FirstTerminal")) {
            firstTerminal = NbtUtils.readBlockPos(tag.getCompound("FirstTerminal"));
            if (!validateAxis(level, firstTerminal) || !firstTerminal.closerThan(pos, maxWalkwayLength() * 2)) {
                tag.remove("FirstTerminal");
                context.getItemInHand().setTag(tag);
            }
        }

        if (!validAxis || playerEntity == null)
            return InteractionResult.FAIL;

        if (tag.contains("FirstTerminal")) {
            if (!this.canConnect(level, firstTerminal, pos))
                return InteractionResult.FAIL;
            if (firstTerminal != null && !firstTerminal.equals(pos)) {
                this.createSteps(level, firstTerminal, pos);
//                AllAdvancements.BELT.awardTo(playerEntity); // TODO: advancements?
                if (!playerEntity.isCreative())
                    context.getItemInHand().shrink(1);
            }
            if (!context.getItemInHand().isEmpty()) {
                context.getItemInHand().setTag(null);
                playerEntity.getCooldowns().addCooldown(this, 5);
            }
            return InteractionResult.SUCCESS;
        }

        tag.put("FirstTerminal", NbtUtils.writeBlockPos(pos));
        context.getItemInHand().setTag(tag);
        playerEntity.getCooldowns().addCooldown(this, 5);
        return InteractionResult.SUCCESS;
    }

    public int maxWalkwayLength() { return EscalatedConfigs.SERVER.maxWalkwayLength.get(); }
    public int maxEscalatorHeight() { return EscalatedConfigs.SERVER.maxEscalatorHeight.get(); }
    public int maxWalkwayWidth() { return EscalatedConfigs.SERVER.maxWalkwayWidth.get(); }
    public int maxEscalatorWidth() { return EscalatedConfigs.SERVER.maxEscalatorWidth.get(); }

    public static boolean validateAxis(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return level.isLoaded(pos) && (ShaftBlock.isShaft(state) || state.getBlock() instanceof WalkwayBlock);
    }

    public boolean canConnect(Level level, BlockPos first, BlockPos second) {
        if (!level.isLoaded(first) || !level.isLoaded(second))
            return false;

        BlockState firstState = level.getBlockState(first);
        BlockState secondState = level.getBlockState(second);
        Direction.Axis shaftAxis = Direction.Axis.Y;
        if (ShaftBlock.isShaft(firstState)) {
            shaftAxis = firstState.getValue(BlockStateProperties.AXIS);
        } else if (firstState.getBlock() instanceof KineticBlock kinetic && kinetic instanceof WalkwayBlock) {
            shaftAxis = kinetic.getRotationAxis(firstState);
        }

        if (shaftAxis == Direction.Axis.Y)
            return false;
        BlockPos diff = second.subtract(first);
        int x = diff.getX();
        int y = diff.getY();
        int z = diff.getZ();
        boolean escalator = y != 0;

        // Walkway extension
        if (Math.abs(shaftAxis.choose(x, y, z)) == 1) {
            BlockPos actualDiff = new BlockPos(shaftAxis.choose(x, 0, 0), 0, shaftAxis.choose(0, 0, z));
            if (!(ShaftBlock.isShaft(firstState) && secondState.getBlock() instanceof WalkwayBlock
                    || firstState.getBlock() instanceof WalkwayBlock && ShaftBlock.isShaft(secondState)))
                return false;
            Direction.Axis secondAxis = Direction.Axis.Y;
            if (ShaftBlock.isShaft(secondState)) {
                secondAxis = secondState.getValue(BlockStateProperties.AXIS);
            } else if (secondState.getBlock() instanceof KineticBlock kinetic) {
                secondAxis = kinetic.getRotationAxis(secondState);
                actualDiff = actualDiff.multiply(-1);
            }
            if (shaftAxis != secondAxis)
                return false;

            List<BlockPos> list = new ArrayList<>();
            float matchSpeed = 0;
            int width = 1;
            if (level.getBlockEntity(first) instanceof WalkwayBlockEntity walkwayBE) {
                list = walkwayBE.getAllBlocks();
                matchSpeed = walkwayBE.getTheoreticalSpeed();
                width = walkwayBE.getWalkwayWidth();
            } else if (level.getBlockEntity(second) instanceof WalkwayBlockEntity walkwayBE) {
                list = walkwayBE.getAllBlocks();
                matchSpeed = walkwayBE.getTheoreticalSpeed();
                width = walkwayBE.getWalkwayWidth();
            }
            if (list.isEmpty())
                return false;
            int sz = list.size();
            // Check if escalator
            int y1 = list.get(0).getY();
            for (BlockPos pos : list) {
                if (pos.getY() != y1) {
                    escalator = true;
                    break;
                }
            }
            int MAX_WIDTH = escalator ? this.maxEscalatorWidth() : this.maxWalkwayWidth();
            if (width >= MAX_WIDTH)
                return false;
            for (int i = 0; i < sz; ++i) {
                BlockPos pos = list.get(i);
                BlockPos destPos = pos.offset(actualDiff);
                BlockState currentState = level.getBlockState(destPos);
                boolean correctShaft = ShaftBlock.isShaft(currentState) && currentState.getValue(BlockStateProperties.AXIS) == shaftAxis;
                boolean empty = currentState.canBeReplaced();
                if ((i == 0 || i == sz - 1) && !correctShaft || !correctShaft && !empty)
                    return false;
                if (escalator && i != 0 && i != sz - 1 && !empty)
                    return false;
                if (correctShaft) {
                    if (!(level.getBlockEntity(destPos) instanceof KineticBlockEntity kbe))
                        return false;
                    float speed2 = kbe.getTheoreticalSpeed();
                    if (Math.signum(matchSpeed) != Math.signum(speed2) && matchSpeed != 0 && speed2 != 0)
                        return false;
                }
            }
            return true;
        }
        if (escalator && Math.abs(second.getY() - first.getY()) > this.maxEscalatorHeight() || !escalator && !second.closerThan(first, this.maxWalkwayLength()))
            return false;

        if (shaftAxis.choose(x, y, z) != 0)
            return false;
        if (escalator && Math.abs(x) != Math.abs(y) + 3 && Math.abs(z) != Math.abs(y) + 3) // Escalator checking
            return false;

        if (!ShaftBlock.isShaft(secondState) || shaftAxis != secondState.getValue(BlockStateProperties.AXIS))
            return false;

        if (!(level.getBlockEntity(first) instanceof KineticBlockEntity kbe) || !(level.getBlockEntity(second) instanceof KineticBlockEntity kbe1))
            return false;

        // Rotation speed compatibility
        float speed1 = kbe.getTheoreticalSpeed();
        float speed2 = kbe1.getTheoreticalSpeed();
        if (Math.signum(speed1) != Math.signum(speed2) && speed1 != 0 && speed2 != 0)
            return false;

        BlockPos step = BlockPos.containing(Math.signum(diff.getX()), Math.signum(diff.getY()), Math.signum(diff.getZ()));
        if (escalator) { // Check blocks off the main escalator diagonal and at the ends of the diagonal
            boolean firstLower = y > 0;
            BlockPos lowerPos = firstLower ? first : second;
            BlockPos upperPos = firstLower ? second : first;
            int sgn = firstLower ? 1 : -1;

            List<BlockPos> specialPos = new ArrayList<>();
            specialPos.add(lowerPos = lowerPos.offset(sgn * step.getX(), 0, sgn * step.getZ()));
            specialPos.add(upperPos.offset(sgn * -step.getX(), 0, sgn * -step.getZ()));
            specialPos.add(upperPos = upperPos.offset(sgn * -step.getX() * 2, 0, sgn * -step.getZ() * 2));

            for (BlockPos pos : specialPos) {
                BlockState blockState = level.getBlockState(pos);
                if (ShaftBlock.isShaft(blockState) && blockState.getValue(AbstractSimpleShaftBlock.AXIS) == shaftAxis
                    && (pos.equals(lowerPos) || pos.equals(upperPos)))
                    continue;
                if (!blockState.canBeReplaced())
                    return false;
            }
            first = firstLower ? lowerPos : upperPos;
            second = firstLower ? upperPos : lowerPos;
        }
        int LIMIT = 1000;
        for (BlockPos currentPos = first.offset(step); !currentPos.equals(second) && LIMIT-- > 0; currentPos = currentPos.offset(step)) {
            BlockState blockState = level.getBlockState(currentPos);
            if (!escalator && ShaftBlock.isShaft(blockState) && blockState.getValue(AbstractSimpleShaftBlock.AXIS) == shaftAxis)
                continue;
            if (!blockState.canBeReplaced())
                return false;
        }
        return true;
    }

    public void createSteps(Level level, BlockPos start, BlockPos end) {
        level.playSound(null, BlockPos.containing(VecHelper.getCenterOf(start.offset(end))
                .scale(.5f)), this.getPlaceSoundEvent(), SoundSource.BLOCKS, 0.5F, 1F);

        BlockPos diff = end.subtract(start);
        int x = diff.getX();
        int y = diff.getY();
        int z = diff.getZ();
        boolean escalator = y != 0;

        BlockState firstState = level.getBlockState(start);
        Direction.Axis shaftAxis = Direction.Axis.Y;
        if (ShaftBlock.isShaft(firstState)) {
            shaftAxis = firstState.getValue(BlockStateProperties.AXIS);
        } else if (firstState.getBlock() instanceof KineticBlock kinetic && kinetic instanceof WalkwayBlock) {
            shaftAxis = kinetic.getRotationAxis(firstState);
        }
        if (shaftAxis == Direction.Axis.Y)
            return;

        // Walkway extension
        if (Math.abs(shaftAxis.choose(x, y, z)) == 1) {
            BlockPos actualDiff = new BlockPos(shaftAxis.choose(x, 0, 0), 0, shaftAxis.choose(0, 0, z));
            List<BlockPos> list = new ArrayList<>();
            BlockPos referencePos = start;
            if (level.getBlockEntity(start) instanceof WalkwayBlockEntity walkwayBE) {
                list = walkwayBE.getAllBlocks();
                referencePos = walkwayBE.widthReferencePos;
            } else if (level.getBlockEntity(end) instanceof WalkwayBlockEntity walkwayBE) {
                list = walkwayBE.getAllBlocks();
                actualDiff = actualDiff.multiply(-1);
                referencePos = walkwayBE.widthReferencePos;
            }
            if (list.isEmpty())
                return;
            Collections.reverse(list);
            int sz = list.size();
            Direction face = Direction.getNearest(actualDiff.getX(), actualDiff.getY(), actualDiff.getZ());

            for (int i = 0; i < sz; ++i) {
                BlockPos srcPos = list.get(i);
                BlockPos destPos = srcPos.offset(actualDiff);
                BlockState srcState = level.getBlockState(srcPos);
                BlockState destState = level.getBlockState(destPos);
                boolean isShaft = ShaftBlock.isShaft(destState);
                if (!(srcState.getBlock() instanceof WalkwayBlock walkwaySrc) || !(level.getBlockEntity(srcPos) instanceof WalkwayBlockEntity walkwayBE))
                    continue;
                DyeColor color = walkwayBE.getColor();
                float visualProgress = walkwayBE.getVisualProgress();

                Direction walkwayFacing = walkwaySrc.getFacing(srcState);
                boolean left = walkwayFacing.getCounterClockWise() == face;
                if (i == 0)
                    left = !left;
                boolean srcShaft = walkwaySrc.hasWalkwayShaft(srcState);
                // Place blocks
                BlockState replaceSrcState = walkwaySrc.transformFromMerge(level, srcState, srcPos, left, srcShaft, false);
                BlockState placeState = walkwaySrc.transformFromMerge(level, srcState, srcPos, !left, isShaft, false);
                KineticBlockEntity.switchToBlockState(level, srcPos, replaceSrcState);
                KineticBlockEntity.switchToBlockState(level, destPos, placeState);

                // Set block data and prepare for refresh
                if (level.getBlockEntity(srcPos) instanceof WalkwayBlockEntity newWalkwayBE) {
                    newWalkwayBE.applyColor(color);
                    newWalkwayBE.setVisualProgress(visualProgress);
                    newWalkwayBE.widthReferencePos = referencePos;
                    newWalkwayBE.resetClientRender = true;
                    newWalkwayBE.notifyUpdate();
                }
                if (level.getBlockEntity(destPos) instanceof WalkwayBlockEntity destWalkwayBE) {
                    destWalkwayBE.applyColor(color);
                    destWalkwayBE.setVisualProgress(visualProgress);
                    destWalkwayBE.widthReferencePos = referencePos;
                    destWalkwayBE.resetClientRender = true;
                    destWalkwayBE.notifyUpdate();
                }
            }
            // Move handrail
            // Check if handrail can be moved, if not destroy
            for (int i = 0; i < sz; ++i) {
                BlockPos srcPos = list.get(i);
                BlockPos destPos = srcPos.offset(actualDiff);
                BlockPos aboveSrcPos = srcPos.above();
                BlockState aboveSrcState = level.getBlockState(aboveSrcPos);
                if (aboveSrcState.getBlock() instanceof AbstractHandrailBlock) {
                    BlockPos aboveDestPos = destPos.above();
                    if (!level.getBlockState(aboveDestPos).canBeReplaced()) {
                        level.destroyBlock(aboveSrcPos, true);
                        return;
                    }
                }
            }
            // Move handrail if valid
            for (int i = 0; i < sz; ++i) {
                BlockPos srcPos = list.get(i);
                BlockPos destPos = srcPos.offset(actualDiff);
                BlockPos aboveSrcPos = srcPos.above();
                BlockState aboveSrcState = level.getBlockState(aboveSrcPos);
                if (aboveSrcState.getBlock() instanceof AbstractHandrailBlock) {
                    BlockPos aboveDestPos = destPos.above();
                    int width = 1;
                    Direction forward = aboveSrcState.getValue(AbstractHandrailBlock.FACING);
                    Direction handrailLeft = forward.getCounterClockWise();
                    if (aboveSrcState.getValue(AbstractHandrailBlock.SIDE) == AbstractHandrailBlock.Side.BOTH) { // Narrow
                        AbstractHandrailBlock.Side destSide = face == handrailLeft ?
                                AbstractHandrailBlock.Side.LEFT : AbstractHandrailBlock.Side.RIGHT;
                        AbstractHandrailBlock.Side srcSide = destSide == AbstractHandrailBlock.Side.LEFT ?
                                AbstractHandrailBlock.Side.RIGHT : AbstractHandrailBlock.Side.LEFT;
                        level.setBlock(aboveSrcPos, ProperWaterloggedBlock.withWater(level,
                                aboveSrcState.setValue(AbstractHandrailBlock.SIDE, srcSide), aboveSrcPos), 3);
                        level.setBlock(aboveDestPos, ProperWaterloggedBlock.withWater(level,
                                aboveSrcState.setValue(AbstractHandrailBlock.SIDE, destSide), aboveDestPos), 3);
                        // No setting width since it should be 1
                    } else { // Wide
                        level.setBlock(aboveDestPos, ProperWaterloggedBlock.withWater(level, aboveSrcState, aboveDestPos), 3);
                        if (level.getBlockEntity(aboveSrcPos) instanceof HandrailBlockEntity handrailBE) {
                            handrailBE.propagateBreak = false;
                            width = handrailBE.width;
                        }
                        level.setBlock(aboveSrcPos, ProperWaterloggedBlock.withWater(level, Blocks.AIR.defaultBlockState(), aboveSrcPos), 3);
                    }
                    // Set new width
                    ++width;
                    if (level.getBlockEntity(aboveDestPos) instanceof HandrailBlockEntity handrailBE)
                        handrailBE.width = width;
                    // Sync new width to other side
                    BlockPos oppositePos = aboveDestPos.relative(face.getOpposite(), width - 1);
                    if (level.getBlockEntity(oppositePos) instanceof HandrailBlockEntity handrailBE)
                        handrailBE.width = width;
                }
            }
            return;
        }

        Direction facing = getFacingFromTo(start, end);

        KineticBlockEntity.switchToBlockState(level, start, EscalatedBlocks.METAL_WALKWAY_TERMINAL.getDefaultState()
                .setValue(WalkwayTerminalBlock.HORIZONTAL_FACING, facing));
        KineticBlockEntity.switchToBlockState(level, end, EscalatedBlocks.METAL_WALKWAY_TERMINAL.getDefaultState()
                .setValue(WalkwayTerminalBlock.HORIZONTAL_FACING, facing.getOpposite()));

        List<BlockPos> walkwaysToCreate = getWalkwayChainBetween(start, end, escalator, facing);

        if (escalator && end.subtract(start).getY() <= 0)
            facing = facing.getOpposite();

        boolean failed = false;

        for (BlockPos pos : walkwaysToCreate) {
            BlockState existingBlock = level.getBlockState(pos);
            if (existingBlock.getDestroySpeed(level, pos) == -1) {
                failed = true;
                break;
            }
            BlockState shaftState = level.getBlockState(pos);
            boolean existingShaft = ShaftBlock.isShaft(shaftState);
            if (!existingBlock.canBeReplaced())
                level.destroyBlock(pos, false);
            BlockState placeState = this.getPlacedWalkwayBlock(escalator, facing, existingShaft);
            KineticBlockEntity.switchToBlockState(level, pos, placeState);
        }

        if (!failed) {
            if (escalator) {
                boolean firstLower = end.subtract(start).getY() > 0;
                BlockPos lowerPos = firstLower ? start : end;
                BlockPos upperPos = firstLower ? end : start;

                BlockPos horizontalPos = upperPos.relative(facing, -1);
                BlockState horizontalState = level.getBlockState(horizontalPos);
                KineticBlockEntity.switchToBlockState(level, horizontalPos, horizontalState.setValue(WalkwayBlock.SLOPE, WalkwaySlope.HORIZONTAL));

                BlockPos topPos = upperPos.relative(facing, -2);
                BlockState topState = level.getBlockState(topPos);
                KineticBlockEntity.switchToBlockState(level, topPos, topState.setValue(WalkwayBlock.SLOPE, WalkwaySlope.TOP));

                BlockPos bottomPos = lowerPos.relative(facing);
                BlockState bottomState = level.getBlockState(bottomPos);
                KineticBlockEntity.switchToBlockState(level, bottomPos, bottomState.setValue(WalkwayBlock.SLOPE, WalkwaySlope.BOTTOM));
            }
            return;
        }

        for (BlockPos pos : walkwaysToCreate) {
            BlockState failedState = level.getBlockState(pos);
            if (failedState.getBlock() instanceof WalkwayBlock)
                level.destroyBlock(pos, false);
        }
    }

    protected BlockState getPlacedWalkwayBlock(boolean escalator, Direction facing, boolean shaft) {
        // TODO wooden step blocks
        if (escalator) {
            return EscalatedBlocks.METAL_NARROW_ESCALATOR.getDefaultState()
                    .setValue(AbstractWalkwayBlock.SLOPE, WalkwaySlope.MIDDLE)
                    .setValue(AbstractWalkwayBlock.HORIZONTAL_FACING, facing);
        } else {
            return EscalatedBlocks.METAL_NARROW_WALKWAY.getDefaultState()
                    .setValue(AbstractWalkwayBlock.HORIZONTAL_FACING, facing)
                    .setValue(WalkwayBlock.CAPS, shaft ? WalkwayCaps.NONE : WalkwayCaps.NO_SHAFT);
        }
    }

    protected SoundEvent getPlaceSoundEvent() { return SoundEvents.CHAIN_PLACE; }

    private static Direction getFacingFromTo(BlockPos start, BlockPos end) {
        Direction.Axis beltAxis = start.getX() == end.getX() ? Direction.Axis.Z : Direction.Axis.X;
        BlockPos diff = end.subtract(start);
        Direction.AxisDirection axisDirection = beltAxis.choose(diff.getX(), 0, diff.getZ()) > 0
                ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        return Direction.get(axisDirection, beltAxis);
    }

    private static List<BlockPos> getWalkwayChainBetween(BlockPos start, BlockPos end, boolean escalator, Direction direction) {
        List<BlockPos> positions = new LinkedList<>();
        int limit = 1000;

        if (escalator) {
            boolean firstLower = end.subtract(start).getY() > 0;
            BlockPos lowerPos = firstLower ? start : end;
            BlockPos upperPos = firstLower ? end : start;
            if (!firstLower)
                direction = direction.getOpposite();

            positions.add(upperPos.relative(direction, -1));
            positions.add(upperPos.relative(direction, -2));
            start = lowerPos.relative(direction);
            end = upperPos.relative(direction, -2);
        }

        BlockPos current = start;
        do {
            positions.add(current);
            current = current.relative(direction);
            if (escalator)
                current = current.above();
        } while (!current.equals(end) && limit-- > 0);

        if (!escalator)
            positions.remove(start);
        return positions;
    }

    @Override
    public void registerBlocks(Map<Block, Item> map, Item item) {
        super.registerBlocks(map, item);
        for (Block b : this.otherBlocks)
            map.put(b, item);
    }

}
