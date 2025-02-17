package rbasamoyai.escalated.walkways;

import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.foundation.utility.NBTHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import rbasamoyai.escalated.walkways.WalkwayMovementHandler.TransportedEntityInfo;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.simibubi.create.content.kinetics.belt.BeltSlope.HORIZONTAL;

/**
 * Somewhat adapated from {@link com.simibubi.create.content.kinetics.belt.BeltBlockEntity}
 */
public class WalkwayBlockEntity extends KineticBlockEntity {

    public Map<Entity, TransportedEntityInfo> passengers;
    public int walkwayLength;
    protected BlockPos controller;
    public float visualProgress = 0;

    private DyeColor color;

    // TODO items? [loader sided]

    public WalkwayBlockEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.color = null;
    }

    @Override
    public void tick() {
        if (this.walkwayLength == 0)
            WalkwayBlock.initWalkway(this.level, this.worldPosition);

        super.tick();

        if (!(this.level.getBlockState(this.worldPosition).getBlock() instanceof WalkwayBlock walkway))
            return;

        if (!this.isController())
            return;

        this.visualProgress += this.getWalkwayMovementSpeed();
        if (Math.abs(this.visualProgress) > 0.5f) // reset offset
            this.visualProgress = Math.signum(this.visualProgress) * (Math.abs(this.visualProgress) % 0.5f);

        if (this.getSpeed() == 0)
            return;

        // TODO items?

        // TODO transport entities on walkway
        // Move Entities
        if (this.passengers == null)
            this.passengers = new HashMap<>();

        boolean beltFlag = walkway.getWalkwaySlope(this.getBlockState()) != HORIZONTAL;
        for (Iterator<Map.Entry<Entity, TransportedEntityInfo>> iter = this.passengers.entrySet().iterator(); iter.hasNext(); ) {
            Map.Entry<Entity, TransportedEntityInfo> entry = iter.next();
            Entity entity = entry.getKey();
            TransportedEntityInfo info = entry.getValue();
            boolean canBeTransported = WalkwayMovementHandler.canBeTransported(entity);
            boolean leftTheBelt = info.getTicksSinceLastCollision() > (beltFlag ? 3 : 1);
            if (!canBeTransported || leftTheBelt) {
                iter.remove();
                continue;
            }

            info.tick();
            WalkwayMovementHandler.transportEntity(this, entity, info);
        }
    }

    @Override public float calculateStressApplied() { return this.isController() ? super.calculateStressApplied() : 0; }

    @Override
    public AABB createRenderBoundingBox() {
        return super.createRenderBoundingBox().inflate(1);
    }

    @Override
    public void clearKineticInformation() {
        super.clearKineticInformation();
        this.walkwayLength = 0;
        this.controller = null;
    }

    public WalkwayBlockEntity getControllerBE() {
        if (this.controller == null || !this.level.isLoaded(this.controller))
            return null;
        return this.controller != null && this.level.isLoaded(this.controller)
                && this.level.getBlockEntity(this.controller) instanceof WalkwayBlockEntity walkway ? walkway : null;
    }

    public void setController(BlockPos controller) { this.controller = controller; }
    public BlockPos getController() { return this.controller == null ? this.worldPosition : this.controller; }

    public boolean isController() {
        return this.controller != null && this.worldPosition.getX() == this.controller.getX()
                && this.worldPosition.getY() == this.controller.getY() && this.worldPosition.getZ() == this.controller.getZ();
    }

    public boolean isEscalator() {
        return false; // TODO escalator code
    }

    @Override
    protected boolean canPropagateDiagonally(IRotate block, BlockState state) {
        return super.canPropagateDiagonally(block, state);
    }

    @Override
    public float propagateRotationTo(KineticBlockEntity target, BlockState stateFrom, BlockState stateTo, BlockPos diff, boolean connectedViaAxes, boolean connectedViaCogs) {
        if (target instanceof WalkwayBlockEntity walkway && !connectedViaAxes)
            return this.getController().equals(walkway.getController()) ? 1 : 0;
        return 0;
    }

    public float getWalkwayMovementSpeed() { return this.getSpeed() / 480f; }

    public float getVisualProgress() {
        WalkwayBlockEntity controller = this.getControllerBE();
        return controller == null ? 0 : controller.visualProgress;
    }

    public boolean applyColor(@Nullable DyeColor colorIn) {
        if (colorIn == this.color)
            return false;
        if (this.level.isClientSide)
            return true;

        for (BlockPos pos : WalkwayBlock.getWalkwayChain(this.level, this.getController())) {
            WalkwayBlockEntity walkway = WalkwayHelper.getSegmentBE(this.level, pos);
            if (walkway == null)
                continue;
            walkway.color = colorIn;
            walkway.notifyUpdate();
        }
        return true;
    }

    @Nullable public DyeColor getColor() { return this.color; }

    @Override
    protected void write(CompoundTag compound, boolean clientPacket) {
        if (this.controller != null)
            compound.put("Controller", NbtUtils.writeBlockPos(this.controller));
        compound.putBoolean("IsController", this.isController());
        compound.putInt("Length", this.walkwayLength);
        if (this.isController())
            compound.putFloat("VisualProgress", this.visualProgress);

        if (this.color != null)
            NBTHelper.writeEnum(compound, "Dye", this.color);

        super.write(compound, clientPacket);
    }

    @Override
    protected void read(CompoundTag compound, boolean clientPacket) {
        int prevWalkwayLength = this.walkwayLength;

        super.read(compound, clientPacket);

        if (compound.getBoolean("IsController")) {
            this.controller = this.worldPosition;
            this.visualProgress = compound.getFloat("VisualProgress");
        }

        this.color = compound.contains("Dye", Tag.TAG_STRING) ? NBTHelper.readEnum(compound, "Dye", DyeColor.class) : null;

        if (!this.wasMoved) {
            if (!this.isController())
                this.controller = NbtUtils.readBlockPos(compound.getCompound("Controller"));
            this.walkwayLength = compound.getInt("Length");
            if (prevWalkwayLength != this.walkwayLength) {
                // TODO relight?
//                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
//                    if (lighter != null) {
//                        lighter.initializeLight();
//                    }
//                });
            }
        }

        if (!clientPacket)
            return;
    }

}
