package rbasamoyai.escalated.walkways;

import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.BeltBlock;
import com.simibubi.create.content.kinetics.belt.BeltPart;
import com.simibubi.create.content.kinetics.belt.BeltSlope;
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
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import rbasamoyai.escalated.index.EscalatedBlocks;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Adpated from {@link com.simibubi.create.content.kinetics.belt.item.BeltConnectorItem}.
 * If you want to make your own walkway type, extend this item.
 */
public class WalkwayConnectorItem extends BlockItem {

    private final boolean wooden;

    public WalkwayConnectorItem(Properties properties, boolean wooden) {
        super(EscalatedBlocks.WALKWAY_TERMINAL.get(), properties);
        this.wooden = wooden;
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
            if (!validateAxis(level, firstTerminal) || !firstTerminal.closerThan(pos, maxLength() * 2)) {
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
                this.createSteps(level, firstTerminal, pos); // TODO functionality
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

    public int maxLength() { return 25; } // TODO config
    // TODO max width

    public static boolean validateAxis(Level level, BlockPos pos) {
        return level.isLoaded(pos) && ShaftBlock.isShaft(level.getBlockState(pos));
    }

    public boolean canConnect(Level level, BlockPos first, BlockPos second) {
        if (!level.isLoaded(first) || !level.isLoaded(second) || !second.closerThan(first, this.maxLength()))
            return false;

        Direction.Axis shaftAxis = level.getBlockState(first).getValue(BlockStateProperties.AXIS);
        if (shaftAxis == Direction.Axis.Y)
            return false;
        BlockPos diff = second.subtract(first);
        int x = diff.getX();
        int y = diff.getY();
        int z = diff.getZ();
        if (shaftAxis.choose(x, y, z) != 0)
            return false;
        boolean escalator = y != 0;
        if (escalator && Math.abs(x) != Math.abs(y) + 3 && Math.abs(z) != Math.abs(y) + 3) // Escalator checking
            return false;
        if (shaftAxis != level.getBlockState(second).getValue(BlockStateProperties.AXIS))
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
                if (ShaftBlock.isShaft(blockState) && blockState.getValue(AbstractSimpleShaftBlock.AXIS) == shaftAxis)
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
            if (ShaftBlock.isShaft(blockState) && blockState.getValue(AbstractSimpleShaftBlock.AXIS) == shaftAxis)
                continue;
            if (!blockState.canBeReplaced())
                return false;
        }
        return true;
    }

    public void createSteps(Level level, BlockPos start, BlockPos end) {
        level.playSound(null, BlockPos.containing(VecHelper.getCenterOf(start.offset(end))
                .scale(.5f)), this.getPlaceSoundEvent(), SoundSource.BLOCKS, 0.5F, 1F);

        BeltSlope slope = getSlopeBetween(start, end);
        Direction facing = getFacingFromTo(start, end);

        BlockPos diff = end.subtract(start);
        if (diff.getX() == diff.getZ())
            facing = Direction.get(facing.getAxisDirection(), level.getBlockState(start)
                    .getValue(BlockStateProperties.AXIS) == Direction.Axis.X ? Direction.Axis.Z : Direction.Axis.X);

        List<BlockPos> beltsToCreate = getBeltChainBetween(start, end, slope, facing);
        BlockState beltBlock = AllBlocks.BELT.getDefaultState();
        boolean failed = false;

        for (BlockPos pos : beltsToCreate) {
            BlockState existingBlock = level.getBlockState(pos);
            if (existingBlock.getDestroySpeed(level, pos) == -1) {
                failed = true;
                break;
            }

            BeltPart part = pos.equals(start) ? BeltPart.START : pos.equals(end) ? BeltPart.END : BeltPart.MIDDLE;
            BlockState shaftState = level.getBlockState(pos);
            boolean pulley = ShaftBlock.isShaft(shaftState);
            if (part == BeltPart.MIDDLE && pulley)
                part = BeltPart.PULLEY;
            if (pulley && shaftState.getValue(AbstractSimpleShaftBlock.AXIS) == Direction.Axis.Y)
                slope = BeltSlope.SIDEWAYS;

            if (!existingBlock.canBeReplaced())
                level.destroyBlock(pos, false);

            KineticBlockEntity.switchToBlockState(level, pos,
                    ProperWaterloggedBlock.withWater(level, beltBlock.setValue(BeltBlock.SLOPE, slope)
                            .setValue(BeltBlock.PART, part)
                            .setValue(BeltBlock.HORIZONTAL_FACING, facing), pos));
        }

        if (!failed)
            return;

        for (BlockPos pos : beltsToCreate)
            if (AllBlocks.BELT.has(level.getBlockState(pos)))
                level.destroyBlock(pos, false);
    }

    protected SoundEvent getPlaceSoundEvent() { return SoundEvents.CHAIN_PLACE; }

    private static Direction getFacingFromTo(BlockPos start, BlockPos end) {
        Direction.Axis beltAxis = start.getX() == end.getX() ? Direction.Axis.Z : Direction.Axis.X;
        BlockPos diff = end.subtract(start);
        Direction.AxisDirection axisDirection = Direction.AxisDirection.POSITIVE;

        if (diff.getX() == 0 && diff.getZ() == 0)
            axisDirection = diff.getY() > 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;
        else
            axisDirection =
                    beltAxis.choose(diff.getX(), 0, diff.getZ()) > 0 ? Direction.AxisDirection.POSITIVE : Direction.AxisDirection.NEGATIVE;

        return Direction.get(axisDirection, beltAxis);
    }

    private static BeltSlope getSlopeBetween(BlockPos start, BlockPos end) {
        BlockPos diff = end.subtract(start);

        if (diff.getY() != 0) {
            if (diff.getZ() != 0 || diff.getX() != 0)
                return diff.getY() > 0 ? BeltSlope.UPWARD : BeltSlope.DOWNWARD;
            return BeltSlope.VERTICAL;
        }
        return BeltSlope.HORIZONTAL;
    }

    private static List<BlockPos> getBeltChainBetween(BlockPos start, BlockPos end, BeltSlope slope,
                                                      Direction direction) {
        List<BlockPos> positions = new LinkedList<>();
        int limit = 1000;
        BlockPos current = start;

        do {
            positions.add(current);

            if (slope == BeltSlope.VERTICAL) {
                current = current.above(direction.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : -1);
                continue;
            }

            current = current.relative(direction);
            if (slope != BeltSlope.HORIZONTAL)
                current = current.above(slope == BeltSlope.UPWARD ? 1 : -1);

        } while (!current.equals(end) && limit-- > 0);

        positions.add(end);
        return positions;
    }

}
