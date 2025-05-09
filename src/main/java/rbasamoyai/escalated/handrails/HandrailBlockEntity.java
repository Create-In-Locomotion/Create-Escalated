package rbasamoyai.escalated.handrails;

import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.data.Iterate;
import net.createmod.catnip.nbt.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import rbasamoyai.escalated.platform.EnvExecute;
import rbasamoyai.escalated.walkways.WalkwayBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class HandrailBlockEntity extends SmartBlockEntity {

    public int width = 1;
    public boolean propagateBreak = true;
    private DyeColor handrailColor = null;

    public HandrailBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {

    }

    public float getSpeed() {
        return this.level != null && this.level.getBlockEntity(this.worldPosition.below()) instanceof WalkwayBlockEntity walkway
                ? walkway.getWalkwayMovementSpeed() : 0;
    }

    public float getVisualProgress() {
        return this.level != null && this.level.getBlockEntity(this.worldPosition.below()) instanceof WalkwayBlockEntity walkway
                ? walkway.getVisualProgress() : 0;
    }

    public boolean setHandrailColor(@Nullable DyeColor colorIn) {
        if (colorIn == this.handrailColor)
            return false;
        if (this.level.isClientSide)
            return true;

        int MAX_ITER = 1100;

        Direction facing = this.getBlockState().getValue(AbstractHandrailBlock.FACING);
        Direction left = facing.getCounterClockWise();
        AbstractHandrailBlock.Side side = this.getBlockState().getValue(AbstractHandrailBlock.SIDE);

        this.handrailColor = colorIn;
        this.notifyUpdate();

        BlockPos offset = BlockPos.ZERO;
        if (this.width > 1) {
            Direction offsetDir = side == AbstractHandrailBlock.Side.LEFT ? left.getOpposite() : left;
            offset = BlockPos.ZERO.relative(offsetDir, this.width - 1);
            if (this.level.getBlockEntity(this.worldPosition.offset(offset)) instanceof HandrailBlockEntity other) {
                other.handrailColor = colorIn;
                other.notifyUpdate();
            }
        }

        for (boolean forward : Iterate.trueAndFalse) {
            BlockPos iterPos = this.worldPosition;
            BlockState iterState = this.getBlockState();

            for (int i = 0; i < MAX_ITER; ++i) {
                iterPos = AbstractHandrailBlock.nextSegmentPosition(iterState, iterPos, forward);
                if (iterPos == null)
                    break;
                iterState = this.level.getBlockState(iterPos);
                if (!(iterState.getBlock() instanceof AbstractHandrailBlock))
                    break;
                if (this.level.getBlockEntity(iterPos) instanceof HandrailBlockEntity other) {
                    other.handrailColor = colorIn;
                    other.notifyUpdate();
                }
                if (this.width > 1 && this.level.getBlockEntity(iterPos.offset(offset)) instanceof HandrailBlockEntity other) {
                    other.handrailColor = colorIn;
                    other.notifyUpdate();
                }
            }
        }
        return true;
    }

    @Nullable public DyeColor getHandrailColor() { return this.handrailColor; }

    @Override
    protected void read(CompoundTag tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.width = tag.getInt("Width");
        this.handrailColor = tag.contains("Dye", Tag.TAG_STRING) ? NBTHelper.readEnum(tag, "Dye", DyeColor.class) : null;

        if (clientPacket)
            EnvExecute.executeOnClient(() -> () -> requestModelDataUpdate());
    }

    @Override
    protected void write(CompoundTag tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.putInt("Width", this.width);
        if (this.handrailColor != null)
            NBTHelper.writeEnum(tag, "Dye", this.handrailColor);
    }

}
