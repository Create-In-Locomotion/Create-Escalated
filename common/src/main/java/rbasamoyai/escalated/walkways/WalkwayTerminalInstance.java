package rbasamoyai.escalated.walkways;

import com.jozufozu.flywheel.api.MaterialManager;
import com.jozufozu.flywheel.api.instance.DynamicInstance;
import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
import net.minecraft.core.Direction;

public class WalkwayTerminalInstance extends KineticBlockEntityInstance<WalkwayTerminalBlockEntity> implements DynamicInstance {

    private RotatingData leftShaft;
    private RotatingData rightShaft;
    private RotatingData bottomShaft;

    public WalkwayTerminalInstance(MaterialManager materialManager, WalkwayTerminalBlockEntity blockEntity) {
        super(materialManager, blockEntity);
    }

    @Override
    public void init() {
        super.init();
        Direction facing = this.blockState.getValue(WalkwayTerminalBlock.HORIZONTAL_FACING);
        WalkwayTerminalBlock.Caps caps = this.blockState.getValue(WalkwayTerminalBlock.CAPS);
        Direction left = facing.getCounterClockWise();
        Direction right = left.getOpposite();

        if (!caps.hasLeftCap()) {
            this.leftShaft = this.setup(this.getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, this.blockState, left)
                    .createInstance(), left.getAxis());
        } else {
            this.leftShaft = null;
        }
        if (!caps.hasRightCap()) {
            this.rightShaft = this.setup(this.getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, this.blockState, right)
                    .createInstance(), right.getAxis());
        } else {
            this.rightShaft = null;
        }
        this.bottomShaft = this.setup(this.getRotatingMaterial().getModel(AllPartialModels.SHAFT_HALF, this.blockState, Direction.DOWN)
                .createInstance(), Direction.Axis.Y);
        // TODO setup steps
        // TODO setup dyeable steps
    }

    @Override
    public void remove() {
        if (this.leftShaft != null)
            this.leftShaft.delete();
        if (this.rightShaft != null)
            this.rightShaft.delete();
        this.bottomShaft.delete();
    }

    @Override
    public void beginFrame() {
        // TODO render steps
    }

    @Override
    public void update() {
        super.update();
        if (this.leftShaft != null)
            this.updateRotation(this.leftShaft);
        if (this.rightShaft != null)
            this.updateRotation(this.rightShaft);
        this.updateRotation(this.bottomShaft, Direction.Axis.Y);
    }

    @Override
    public boolean shouldReset() {
        return super.shouldReset() && this.blockState != this.blockEntity.getBlockState();
    }

    @Override
    public void updateLight() {
        super.updateLight();
        if (this.leftShaft != null)
            this.relight(this.pos, this.leftShaft);
        if (this.rightShaft != null)
            this.relight(this.pos, this.rightShaft);
        this.relight(this.pos, this.bottomShaft);
    }

}
